package br.com.jvsf.banco.banco.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.jvsf.banco.banco.exception.RecursoNaoEncontradoException;
import br.com.jvsf.banco.banco.exception.RegistroExistenteException;
import br.com.jvsf.banco.banco.exception.SaldoInsuficienteException;
import br.com.jvsf.banco.banco.exception.TransferenciaMesmaContaException;
import lombok.Getter;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ResponseEntity<Object> handleRecursoNaoEncontradoException(RecursoNaoEncontradoException ex, WebRequest request) {
		Erro erro = new Erro(ex.getMessage(), ExceptionUtils.getStackTrace(ex));
		return handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(RegistroExistenteException.class)
	public ResponseEntity<Object> handleRegistroExistenteException(RegistroExistenteException ex, WebRequest request) {
		Erro erro = new Erro(ex.getMessage(), ExceptionUtils.getStackTrace(ex));
		return handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler(SaldoInsuficienteException.class)
	public ResponseEntity<Object> handleSaldoInsuficienteException(SaldoInsuficienteException ex, WebRequest request) {
		Erro erro = new Erro(ex.getMessage(), ExceptionUtils.getStackTrace(ex));
		return handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler(TransferenciaMesmaContaException.class)
	public ResponseEntity<Object> handleTransferenciaMesmaContaException(TransferenciaMesmaContaException ex, WebRequest request) {
		Erro erro = new Erro(ex.getMessage(), ExceptionUtils.getStackTrace(ex));
		return handleExceptionInternal(ex, erro, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
																  HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<Erro> erros = criarListaDeErros(ex.getBindingResult());
		return super.handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, 
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<Erro> erros = new ArrayList<>();
		erros.add(new Erro(String.format("Parâmetro %s é obrigatório.", ex.getParameterName())));
		return handleExceptionInternal(ex, erros, headers, status, request);
	}

	private List<Erro> criarListaDeErros(BindingResult bindingResult) {
		List<Erro> erros = new ArrayList<>();

		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			String mensagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
			String mensagemDesenvolvedor = fieldError.toString();
			erros.add(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		}

		return erros;
	}

	@Getter
	public static class Erro {

		private String mensagemUsuario;
		private String mensagemDesenvolvedor;
		
		@JsonCreator
		public Erro(@JsonProperty("mensagemUsuario") String mensagemUsuario) {
			this(mensagemUsuario, null);
		}

		public Erro(String mensagemUsuario, String mensagemDesenvolvedor) {
			this.mensagemUsuario = mensagemUsuario;
			this.mensagemDesenvolvedor = mensagemDesenvolvedor;
		}

	}

}
