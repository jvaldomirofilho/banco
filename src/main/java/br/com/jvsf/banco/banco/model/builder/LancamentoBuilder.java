package br.com.jvsf.banco.banco.model.builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.jvsf.banco.banco.model.Conta;
import br.com.jvsf.banco.banco.model.Lancamento;
import br.com.jvsf.banco.banco.model.enums.Operacao;
import br.com.jvsf.banco.banco.model.enums.StatusLancamento;

public class LancamentoBuilder {

	private Lancamento lancamento;
	
	public LancamentoBuilder(){
		this.lancamento = new Lancamento();
	}
	
	public LancamentoBuilder comContaRemetente(Conta contaRemetente) {
		this.lancamento.setContaRemetente(contaRemetente);
		return this;
	}

	public LancamentoBuilder comContaFavorecido(Conta contaFavorecido) {
		this.lancamento.setContaFavorecido(contaFavorecido);
		return this;
	}

	
	public LancamentoBuilder comOperacao(Operacao operacao) {
		this.lancamento.setOperacao(operacao);
		return this;
	}
	public LancamentoBuilder comStatus(StatusLancamento status) {
		this.lancamento.setStatus(status);
		return this;
	}
	public LancamentoBuilder comDataLancamento(LocalDateTime dataLancamento) {
		this.lancamento.setDataLancamento(dataLancamento);;
		return this;
	}
	public LancamentoBuilder comValor(BigDecimal valor) {
		this.lancamento.setValor(valor);
		return this;
	}
	public LancamentoBuilder comDescricao(String descricao) {
		this.lancamento.setDescricao(descricao);
		return this;
	}
	
	public Lancamento criar() {
		return this.lancamento;
	}
	
}
