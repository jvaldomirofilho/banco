package br.com.jvsf.banco.banco.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.jvsf.banco.banco.dto.ContaDTO;
import br.com.jvsf.banco.banco.dto.LancamentoDTO;
import br.com.jvsf.banco.banco.dto.SaldoDTO;
import br.com.jvsf.banco.banco.form.ContaForm;
import br.com.jvsf.banco.banco.form.DepositoForm;
import br.com.jvsf.banco.banco.form.LancamentoForm;
import br.com.jvsf.banco.banco.repository.filter.ContaFilter;
import br.com.jvsf.banco.banco.service.ContaService;

@RestController
@RequestMapping("/contas")
public class ContaController {

	@Autowired
	private ContaService service;
	
	@GetMapping(produces="application/json")
	public Page<ContaDTO> buscarPorFiltro(ContaFilter filter, @PageableDefault Pageable pageable){
		return service.buscarPorFiltro(filter, pageable);
	}
	
	@GetMapping(path =  "/saldo", produces="application/json")
	public SaldoDTO consultaSaldo(@RequestParam String agencia, @RequestParam String numero) {
		return service.consultarSaldo(new ContaFilter(agencia, numero));
	} 
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ContaDTO cadastrarConta(@RequestBody @Valid ContaForm contaForm){
		return service.salvar(contaForm);
	}
	
	@PostMapping(path = "/deposito")
	@ResponseStatus(HttpStatus.CREATED)
	public LancamentoDTO depositar(@RequestBody @Valid DepositoForm depositoForm) {
		return service.depositar(depositoForm);
	}
	
	@PostMapping(path = "/transferencia")
	@ResponseStatus(HttpStatus.CREATED)
	public LancamentoDTO transferir(@RequestBody @Valid LancamentoForm lancamentoForm) {
		return service.transferir(lancamentoForm);
	}
	
	@PutMapping(path = "/transferencia/{idLancamento}/reverte")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void reverterTransferencia(@PathVariable Long idLancamento) {
		service.reverterTransferencia(idLancamento);
	}
	
	@PostMapping(path = "/agendamentos")
	@ResponseStatus(HttpStatus.CREATED)
	public List<LancamentoDTO> agendarTransferencia(@RequestBody @Valid LancamentoForm lancamentoForm) {
		return service.agendarTransferencia(lancamentoForm);
	}
	
	@GetMapping(path = "/agendamentos", produces="application/json")
	public List<LancamentoDTO> buscarAgendamentos(@RequestParam String agencia, @RequestParam String numero) {
		return service.buscarLancamentosAgendados(new ContaFilter(agencia, numero));
	}
	

}
