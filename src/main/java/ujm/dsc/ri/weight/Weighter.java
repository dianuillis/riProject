package ujm.dsc.ri.weight;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import ujm.dsc.ri.core.Doc;
import ujm.dsc.ri.core.DocumentsCollection;
import ujm.dsc.ri.core.QueriesCollection;
import ujm.dsc.ri.core.Query;

@Component
public class Weighter {

	private static final Logger log = LogManager.getLogger(Weighter.class.getName());

	public SortedMap<String, SortedMap<Long, Double>> weightDocsLTN(DocumentsCollection documentsCollection,
			QueriesCollection queriesCollection) {
		log.info("Calculating documents weights");
		long start = System.currentTimeMillis();
		SortedMap<String, SortedMap<Long, Double>> docWeights = new TreeMap<>();
		Iterator<Query> qiterator = queriesCollection.getQueries().iterator();
		while (qiterator.hasNext()) {
			Query query = qiterator.next();
			for (Entry<String, Integer> qentry : query.getTerms().entrySet()) {
				SortedMap<Long, Double> termWeight = new TreeMap<>();
				String term = qentry.getKey();
				int df = documentsCollection.getDocumentFequency(term);
				double t = 0;
				if (df != 0)
					t = Math.log10(documentsCollection.getDocuments().size() / df);
				Iterator<Doc> diterator = documentsCollection.getDocuments().iterator();
				while (diterator.hasNext()) {
					Doc document = diterator.next();
					if (document.getTerms().containsKey(term)) {
						int tf = document.getTerms().get(term);
						double l = 1 + Math.log10(tf);
						double w = l * t;
						termWeight.put(document.getId(), w);
					} else {
						termWeight.put(document.getId(), 0.0);
					}
					// pass a copy
					docWeights.put(term, new TreeMap<>(termWeight));
					// termWeight.clear();
				}
			}
		}
		long end = System.currentTimeMillis();
		log.info("Done in " + (end - start) / 1000.00 + " seconds");
		// System.out.println(docWeights);
		return docWeights;
	}

	public SortedMap<String, SortedMap<Long, Double>> weightQueryLTN(DocumentsCollection documentsCollection,
			QueriesCollection queriesCollection) {
		log.info("Calculating documents weights");
		long start = System.currentTimeMillis();
		SortedMap<String, SortedMap<Long, Double>> queryWeights = new TreeMap<>();
		Iterator<Query> qiterator = queriesCollection.getQueries().iterator();
		while (qiterator.hasNext()) {
			Query query = qiterator.next();
			for (Entry<String, Integer> qentry : query.getTerms().entrySet()) {
				SortedMap<Long, Double> termWeights = new TreeMap<>();
				String term = qentry.getKey();
				int df = documentsCollection.getDocumentFequency(term);
				double t = 0;
				if (df != 0)
					// t = Math.log10(documentsCollection.getDocuments().size()
					// / df);
					t = Math.log10(1500 / df);
				int tf = query.getTerms().get(term);
				double l = 1 + Math.log10(tf);
				double w = l * t;
				termWeights.put(query.getId(), w);
				queryWeights.put(term, termWeights);
			}
		}
		long end = System.currentTimeMillis();
		log.info("Done in " + (end - start) / 1000.00 + " seconds");
		// System.out.println(queryWeights);
		return queryWeights;
	}

}
