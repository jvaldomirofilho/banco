package br.com.jvsf.banco.banco.dto;

import java.math.BigDecimal;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SaldoDTO {
	
	private BigDecimal saldo;
	private BigDecimal saldoFuturo;

	public static SaldoDTO toDTO(BigDecimal saldo, BigDecimal saldoFuturo) {
		SaldoDTO dto = new SaldoDTO();
		dto.saldo = saldo;
		dto.saldoFuturo = Objects.nonNull(saldoFuturo) ? saldoFuturo : BigDecimal.ZERO;
		return dto;
	}
}
