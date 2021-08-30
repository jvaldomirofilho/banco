package br.com.jvsf.banco.banco.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.jvsf.banco.banco.model.Conta;

public interface ContaRepository extends JpaRepository<Conta, Long>,  JpaSpecificationExecutor<Conta>{

	Optional<Conta> findByAgenciaAndNumero(String agencia, String numero);
	
}
