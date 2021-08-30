package br.com.jvsf.banco.banco.dto;

import java.math.BigDecimal;

import br.com.jvsf.banco.banco.model.Conta;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ContaDTO {
	
	private Long id;
	private String agencia;
	private String numero;
	private BigDecimal saldo;
	
	public static ContaDTO toDTO(Conta conta) {
		ContaDTO dto = new ContaDTO();
		dto.id = conta.getId();
		dto.agencia = conta.getAgencia();
		dto.numero = conta.getNumero();
		dto.saldo = conta.getSaldo();
		return dto;
	}

}
