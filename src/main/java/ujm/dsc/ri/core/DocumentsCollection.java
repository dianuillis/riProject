package ujm.dsc.ri.core;

import java.io.Serializable;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class DocumentsCollection implements Serializable {

	private static final long serialVersionUID = 26003695614203723L;

	private SortedSet<Doc> documents;

	public DocumentsCollection() {
		super();
		this.documents = new TreeSet<>();
	}

	public void addDocument(Doc document) {
		this.documents.add(document);
	}

	public void clear() {
		this.documents.clear();
	}

	public int getDocumentFequency(String term) {
		int cpt = 0;
		Iterator<Doc> iterator = documents.iterator();
		while (iterator.hasNext()) {
			Doc document = iterator.next();
			if (document.getTerms().containsKey(term))
				cpt++;
		}
		return cpt;
	}

}
