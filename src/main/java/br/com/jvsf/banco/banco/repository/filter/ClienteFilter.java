package br.com.jvsf.banco.banco.repository.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ClienteFilter {

    private String nome;
    private String cpf;

}
