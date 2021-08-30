package br.com.jvsf.banco.banco.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.jvsf.banco.banco.model.Conta;
import br.com.jvsf.banco.banco.model.Lancamento;
import br.com.jvsf.banco.banco.model.enums.Operacao;
import br.com.jvsf.banco.banco.model.enums.StatusLancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{
	
	List<Lancamento> findByContaFavorecidoAndStatusOrderByDataLancamento(Conta contaFavorecido, StatusLancamento status);
	
	Optional<Lancamento> findByIdAndOperacao(Long id, Operacao operacao);

}
