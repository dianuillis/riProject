package ujm.dsc.ri.core;

import java.io.Serializable;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class QueriesCollection implements Serializable {

	private static final long serialVersionUID = 4175054623674176567L;

	private SortedSet<Query> queries;

	public QueriesCollection() {
		super();
		this.queries = new TreeSet<>();
	}

	public void addQuery(Query query) {
		this.queries.add(query);
	}

	public void clear() {
		this.queries.clear();
	}

	public int getTermFequency(String term) {
		int cpt = 0;
		Iterator<Query> iterator = queries.iterator();
		while (iterator.hasNext()) {
			Query query = iterator.next();
			if (query.getTerms().containsKey(term))
				cpt++;
		}
		return cpt;
	}

}
