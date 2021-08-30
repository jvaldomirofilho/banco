package br.com.jvsf.banco.banco.exception;

public class TransferenciaMesmaContaException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public TransferenciaMesmaContaException(String mensagem) {
		super(mensagem);
	}

}
