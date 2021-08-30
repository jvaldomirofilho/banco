package br.com.jvsf.banco.banco.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.jvsf.banco.banco.dto.LancamentoDTO;
import br.com.jvsf.banco.banco.exception.RecursoNaoEncontradoException;
import br.com.jvsf.banco.banco.model.Conta;
import br.com.jvsf.banco.banco.model.Lancamento;
import br.com.jvsf.banco.banco.model.enums.Operacao;
import br.com.jvsf.banco.banco.model.enums.StatusLancamento;
import br.com.jvsf.banco.banco.repository.LancamentoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LancamentoService {

	private final LancamentoRepository repository;
	
	public Lancamento salvar(Lancamento lancamento) {
		return repository.save(lancamento);
	}
	
	public Lancamento buscarPorIdEOperacao(Long id, Operacao operacao) {
		return repository.findByIdAndOperacao(id, operacao)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Lancamento não encontrado"));
	}
	
	public List<LancamentoDTO> buscarLancamentosAgendados(Conta conta, StatusLancamento status){
		return repository.findByContaFavorecidoAndStatusOrderByDataLancamento(conta, status)
								.stream()
								.map(LancamentoDTO::toDTO)
								.collect(Collectors.toList());
	}
	
	public List<LancamentoDTO> salvarTodos(List<Lancamento> lancamentos) {
		List<LancamentoDTO> agendados = new ArrayList<LancamentoDTO>();
		lancamentos.stream().forEach(l -> agendados.add(LancamentoDTO.toDTO(repository.save(l))));
		return agendados;
	}
	
}
