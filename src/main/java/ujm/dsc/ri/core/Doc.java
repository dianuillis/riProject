package ujm.dsc.ri.core;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class Doc implements Serializable, Comparable<Doc> {

	private static final long serialVersionUID = -4609157050129638865L;

	private Long id;

	// term and tf
	private SortedMap<String, Integer> terms;

	public Doc() {
		super();
		this.terms = new TreeMap<>();
	}

	public void addTerm(String term) {
		if (!this.terms.containsKey(term)) {
			this.terms.put(term, 1);
		} else
			this.terms.put(term, this.terms.get(term) + 1);

	}

	public void clear() {
		this.terms.clear();
	}

	@Override
	public int compareTo(Doc document) {
		return (int) (this.id - document.getId());
	}

}
