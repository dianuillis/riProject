package ujm.dsc.ri.core;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

@Component
public class InvertedIndex<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8412622431951128590L;

	// [term[docId,tf]]
	// T is Long for articles or String for sec/p
	private SortedMap<String, SortedMap<T, Double>> terms;

	private double docNbr = 0.0;

	public InvertedIndex() {
		super();
		this.terms = new TreeMap<>();
	}

	public void addTerm(String term, T docId, double alpha) {
		SortedMap<T, Double> tmp = new TreeMap<>();
		if (!this.terms.containsKey(term)) {
			tmp.put(docId, alpha);
		} else {
			tmp = this.terms.get(term);
			if (!tmp.containsKey(docId))
				tmp.put(docId, alpha);
			else
				tmp.put(docId, tmp.get(docId) + alpha);
		}
		this.terms.put(term, tmp);
	}

	public void clear() {
		this.terms.clear();
	}

	public double getDocNbr() {
		return docNbr;
	}

	public SortedMap<String, SortedMap<T, Double>> getTerms() {
		return terms;
	}

	public void setDocNbr(double docNbr) {
		this.docNbr = docNbr;
	}

	public void setTerms(SortedMap<String, SortedMap<T, Double>> terms) {
		this.terms = terms;
	}

}
