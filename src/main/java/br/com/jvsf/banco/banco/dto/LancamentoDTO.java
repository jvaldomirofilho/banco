package br.com.jvsf.banco.banco.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.jvsf.banco.banco.model.Lancamento;
import br.com.jvsf.banco.banco.model.enums.Operacao;
import br.com.jvsf.banco.banco.model.enums.StatusLancamento;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LancamentoDTO {
	
	private Long id;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dataLancamento; 
	private ContaDTO contaRemetente;
	private ContaDTO contaFavorecido;
	private BigDecimal valor;
	private String descricao;
	private Operacao operacao;
	private StatusLancamento status;
	
	public static LancamentoDTO toDTO(Lancamento lancamento) {
		LancamentoDTO dto = new LancamentoDTO();
		dto.id = lancamento.getId();
		dto.operacao = lancamento.getOperacao();
		dto.status = lancamento.getStatus();
		dto.contaRemetente = Objects.nonNull(lancamento.getContaRemetente()) ? ContaDTO.toDTO(lancamento.getContaRemetente()) : null;
		dto.contaFavorecido = ContaDTO.toDTO(lancamento.getContaFavorecido());
		dto.valor = lancamento.getValor();
		dto.descricao = lancamento.getDescricao();
		dto.dataLancamento = lancamento.getDataLancamento();
		return dto;
	}
}

