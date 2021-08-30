package br.com.jvsf.banco.banco.form;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LancamentoForm {
	
	@NotBlank
	private String agenciaRemetente;
	@NotBlank
	private String numeroRemetente;
	
	@NotBlank
	private String agenciaFavorecido;
	@NotBlank
	private String numeroFavorecido;
	
	private String descricao;
	
	private Integer numeroDeParcelas = 1;
	
	@NotNull
	@DecimalMin(value = "0.00", inclusive = false)
	@Digits(integer=11, fraction=2)
	private BigDecimal valor;
	

}
