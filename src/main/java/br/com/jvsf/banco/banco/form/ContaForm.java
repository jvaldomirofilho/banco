package br.com.jvsf.banco.banco.form;

import javax.validation.constraints.NotBlank;

import br.com.jvsf.banco.banco.model.Conta;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ContaForm {
	
	@NotBlank
	private String agencia;
	@NotBlank
	private String numero;

    public void copyToModel(Conta conta) {
        conta.setAgencia(agencia);
        conta.setNumero(numero);
     }

}
