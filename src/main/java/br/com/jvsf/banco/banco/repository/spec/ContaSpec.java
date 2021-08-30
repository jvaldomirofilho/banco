package br.com.jvsf.banco.banco.repository.spec;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import br.com.jvsf.banco.banco.model.Conta;
import br.com.jvsf.banco.banco.repository.filter.ContaFilter;

public class ContaSpec {
	
	public static Specification<Conta> comFiltro(ContaFilter filter) {
		return (root, query, builder) -> {

			List<Predicate> predicates = new ArrayList<>();
			
			if (StringUtils.isNotBlank(filter.getAgencia())) {
				predicates.add(builder.equal(root.get("agencia"), filter.getAgencia().toUpperCase()));
			}

			if (StringUtils.isNotBlank(filter.getNumero())) {
				predicates.add(builder.equal(root.get("numero"),  filter.getNumero().toUpperCase()));
			}

			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
	
	
}
