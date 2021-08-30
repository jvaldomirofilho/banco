package br.com.jvsf.banco.banco.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import br.com.jvsf.banco.banco.model.Conta;
import br.com.jvsf.banco.banco.repository.ContaRepository;

@Configuration
@EnableJpaRepositories(basePackageClasses = ContaRepository.class)
@EntityScan(basePackageClasses = Conta.class)
public class JPAConfig {

}
