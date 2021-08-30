package br.com.jvsf.banco.banco.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import br.com.jvsf.banco.banco.model.enums.Operacao;
import br.com.jvsf.banco.banco.model.enums.StatusLancamento;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Lancamento {
	
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private Operacao operacao;
	
	@Enumerated(EnumType.STRING)
	private StatusLancamento status;
	
	private LocalDateTime dataLancamento;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "conta_remetente_id")
	private Conta contaRemetente;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "conta_favorecido_id")
	private Conta contaFavorecido;
	
	private BigDecimal valor;
	
	private String descricao;

	

}
