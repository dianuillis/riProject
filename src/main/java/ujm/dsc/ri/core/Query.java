package ujm.dsc.ri.core;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class Query implements Serializable, Comparable<Query> {

	private static final long serialVersionUID = -5275066911385257950L;

	private long id;

	private SortedMap<String, Integer> terms;

	public Query() {
		super();
		this.terms = new TreeMap<>();
	}

	public void addTerm(String term) {
		if (!this.terms.containsKey(term))
			this.terms.put(term, 1);
		else
			this.terms.put(term, this.terms.get(term) + 1);
	}

	public void clear() {
		this.terms.clear();
	}

	@Override
	public int compareTo(Query query) {
		return (int) (this.id - query.getId());
	}

}
