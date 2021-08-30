package br.com.jvsf.banco.banco.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Conta {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	private String agencia;
	private String numero;
	private BigDecimal saldo = BigDecimal.ZERO;
	
	@Override
	public String toString() {
		return "Agência: " +agencia + ", Conta:" + numero;
	}

	public Conta(Long id) {
		super();
		this.id = id;
	}
}
