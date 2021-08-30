package br.com.jvsf.banco.banco.exception;

public class RegistroExistenteException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public RegistroExistenteException(String mensagem) {
		super(mensagem);
	}

}
