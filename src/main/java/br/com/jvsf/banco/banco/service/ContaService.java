package br.com.jvsf.banco.banco.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.jvsf.banco.banco.dto.ContaDTO;
import br.com.jvsf.banco.banco.dto.LancamentoDTO;
import br.com.jvsf.banco.banco.dto.SaldoDTO;
import br.com.jvsf.banco.banco.exception.RecursoNaoEncontradoException;
import br.com.jvsf.banco.banco.exception.RegistroExistenteException;
import br.com.jvsf.banco.banco.exception.SaldoInsuficienteException;
import br.com.jvsf.banco.banco.exception.TransferenciaMesmaContaException;
import br.com.jvsf.banco.banco.form.ContaForm;
import br.com.jvsf.banco.banco.form.DepositoForm;
import br.com.jvsf.banco.banco.form.LancamentoForm;
import br.com.jvsf.banco.banco.model.Conta;
import br.com.jvsf.banco.banco.model.Lancamento;
import br.com.jvsf.banco.banco.model.builder.LancamentoBuilder;
import br.com.jvsf.banco.banco.model.enums.Operacao;
import br.com.jvsf.banco.banco.model.enums.StatusLancamento;
import br.com.jvsf.banco.banco.repository.ContaRepository;
import br.com.jvsf.banco.banco.repository.filter.ContaFilter;
import br.com.jvsf.banco.banco.repository.spec.ContaSpec;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContaService {

	private final ContaRepository repository;
	
	private final LancamentoService lancamentoService;
	
	public Page<ContaDTO> buscarPorFiltro(ContaFilter filter, Pageable pageable){
		return repository.findAll(ContaSpec.comFiltro(filter), pageable).map(ContaDTO::toDTO);
	}
	
	public ContaDTO salvar(ContaForm contaForm) {
		Conta conta = new Conta();
		contaForm.copyToModel(conta);
		return ContaDTO.toDTO(salvarOuAtualizar(conta));
	}
	
	public SaldoDTO consultarSaldo(ContaFilter filter) {
		Conta conta = buscarPorAgenciaENumero(filter.getAgencia(), filter.getNumero());
		List<LancamentoDTO> lancamentos = lancamentoService.buscarLancamentosAgendados(conta, StatusLancamento.AGENDADO);
		return SaldoDTO
					.toDTO(conta.getSaldo(), somarLancamentos(lancamentos));
	}
	
	public List<LancamentoDTO> buscarLancamentosAgendados(ContaFilter filter) {
		Conta conta = buscarPorAgenciaENumero(filter.getAgencia(), filter.getNumero());
		return lancamentoService.buscarLancamentosAgendados(conta, StatusLancamento.AGENDADO);
	}

	@Transactional
	public LancamentoDTO depositar(DepositoForm depositoForm) {
		Conta conta = buscarPorAgenciaENumero(depositoForm.getAgencia(), depositoForm.getNumero());
		conta.setSaldo(conta.getSaldo().add(depositoForm.getValor()));
		salvarOuAtualizar(conta);
		Lancamento lancamento = new LancamentoBuilder()
									.comContaFavorecido(conta)
									.comDescricao("DEPOSITO ONLINE")
									.comStatus(StatusLancamento.EFETIVADO)
									.comOperacao(Operacao.DEPOSITO)
									.comValor(depositoForm.getValor())
									.comDataLancamento(LocalDateTime.now())
									.criar();
		return LancamentoDTO.toDTO(lancamentoService.salvar(lancamento));
	}
	
	@Transactional
	public LancamentoDTO transferir(LancamentoForm lancamentoForm) {
		Conta contaRemetente = buscarPorAgenciaENumero(lancamentoForm.getAgenciaRemetente(), lancamentoForm.getNumeroRemetente());
		Conta contaFavorecido = buscarPorAgenciaENumero(lancamentoForm.getAgenciaFavorecido(), lancamentoForm.getNumeroFavorecido());
		validarTransferencia(contaRemetente, contaFavorecido, lancamentoForm.getValor());
		contaRemetente.setSaldo(contaRemetente.getSaldo().subtract(lancamentoForm.getValor()));
		contaFavorecido.setSaldo(contaFavorecido.getSaldo().add(lancamentoForm.getValor()));
		salvarOuAtualizar(contaRemetente);
		salvarOuAtualizar(contaFavorecido);
		Lancamento lancamento = novoLancamentoTransferencia(lancamentoForm, contaRemetente, contaFavorecido);
		return LancamentoDTO.toDTO(lancamentoService.salvar(lancamento));
	}
	
	public void reverterTransferencia(Long idLancamento) {
		Lancamento lancamento = lancamentoService.buscarPorIdEOperacao(idLancamento, Operacao.TRANSFERENCIA);
		if (StatusLancamento.EFETIVADO.equals(lancamento.getStatus())) {
			Conta contaRemetente = lancamento.getContaRemetente();
			Conta contaFavorecido = lancamento.getContaFavorecido();
			contaRemetente.setSaldo(contaRemetente.getSaldo().add(lancamento.getValor()));
			contaFavorecido.setSaldo(contaFavorecido.getSaldo().subtract(lancamento.getValor()));
			salvarOuAtualizar(contaRemetente);
			salvarOuAtualizar(contaFavorecido);
			lancamento.setStatus(StatusLancamento.REVERTIDO);
			lancamentoService.salvar(lancamento);
		}else {
			throw new RegistroExistenteException("Lancamento informado não foi efetivado");
		}
	}
	
	@Transactional
	public List<LancamentoDTO> agendarTransferencia(LancamentoForm lancamentoForm) {
		Conta contaRemetente = buscarPorAgenciaENumero(lancamentoForm.getAgenciaRemetente(), lancamentoForm.getNumeroRemetente());
		Conta contaFavorecido = buscarPorAgenciaENumero(lancamentoForm.getAgenciaFavorecido(), lancamentoForm.getNumeroFavorecido());
		List<Lancamento> agendamentos = new ArrayList<>();
		for(int i = 1; i <= lancamentoForm.getNumeroDeParcelas(); i++) {
			Lancamento lancamento = novoLancamentoTransferencia(lancamentoForm, contaRemetente, contaFavorecido);
			lancamento.setStatus(StatusLancamento.AGENDADO);
			lancamento.setDataLancamento(LocalDateTime.now().plusMonths(i));
			BigDecimal valorParcela = lancamentoForm.getValor()
					.divide(new BigDecimal(lancamentoForm.getNumeroDeParcelas()), RoundingMode.CEILING)
					.setScale(2, RoundingMode.CEILING);
			lancamento.setValor(valorParcela);
			lancamento.setDescricao("Transferencia parcela numero " + i);
			agendamentos.add(lancamento);
		}
		return lancamentoService.salvarTodos(agendamentos);
	}
	
	private Lancamento novoLancamentoTransferencia(LancamentoForm lancamentoForm, Conta contaRemetente, Conta contaFavorecido) {
		return new LancamentoBuilder()
							.comContaRemetente(contaRemetente)
							.comContaFavorecido(contaFavorecido)
							.comDescricao(lancamentoForm.getDescricao())
							.comStatus(StatusLancamento.EFETIVADO)
							.comOperacao(Operacao.TRANSFERENCIA)
							.comValor(lancamentoForm.getValor())
							.comDataLancamento(LocalDateTime.now())
							.criar();
	}
	
	
	private void validarTransferencia(Conta conta, Conta contaFavorecido, BigDecimal valor){
		if (conta.getId().equals(contaFavorecido.getId())) {
			throw new TransferenciaMesmaContaException("Conta favorecido deve ser diferente da conta remetente");
		}
		
		if(conta.getSaldo().compareTo(valor) < 0) {
			throw new SaldoInsuficienteException("Saldo insuficiente");
		}
	}
	
	private Conta salvarOuAtualizar(Conta conta) {
		try {
			return repository.save(conta);
		} catch (DataIntegrityViolationException e) {
			throw new RegistroExistenteException("Conta com Agência: " +conta.getAgencia()+ " e Número: " + conta.getNumero() +" já existe");
		}
	}
	
	private BigDecimal somarLancamentos(List<LancamentoDTO> lancamentos) {
		return lancamentos.stream()
				.map(LancamentoDTO::getValor)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	private Conta buscarPorAgenciaENumero(String agencia, String numero) {
		return repository.findByAgenciaAndNumero(agencia, numero)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada"));
	}
	
	
}
