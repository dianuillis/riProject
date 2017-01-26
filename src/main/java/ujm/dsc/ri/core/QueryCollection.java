package ujm.dsc.ri.core;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

@Component
public class QueryCollection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 772578462319959902L;

	private SortedMap<Long, String[]> terms = new TreeMap<>();

	private SortedSet<String> uniqueTerms = new TreeSet<>();

	public void addTerm(Long queryId, String[] terms) {
		this.terms.put(queryId, terms);
		for (String tmp : terms) {
			uniqueTerms.add(tmp);
		}
	}

	public SortedMap<Long, String[]> getTerms() {
		return terms;
	}

	public SortedSet<String> getUniqueTerms() {
		return uniqueTerms;
	}

	public void setTerms(SortedMap<Long, String[]> terms) {
		this.terms = terms;
	}

	public void setUniqueTerms(SortedSet<String> uniqueTerms) {
		this.uniqueTerms = uniqueTerms;
	}

}
