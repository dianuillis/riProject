package ujm.dsc.ri.weight;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import ujm.dsc.ri.core.InvertedIndex;

@Component
public class Weighter {
	private static final Logger log = LogManager.getLogger(Weighter.class.getName());

	// TODO double the shit in this function
	public <T> SortedMap<String, SortedMap<T, Double>> weightBM25(InvertedIndex<T> index, SortedMap<T, Double> termsNbr,
			double k, double b) {

		double avdl = 0.0;
		for (Map.Entry<T, Double> entry : termsNbr.entrySet()) {
			avdl += entry.getValue();
		}
		avdl = avdl / termsNbr.size();

		SortedMap<String, SortedMap<T, Double>> weights = new TreeMap<>();
		for (Map.Entry<String, SortedMap<T, Double>> entry : index.getTerms().entrySet()) {
			SortedMap<T, Double> tmp = new TreeMap<>();
			for (Map.Entry<T, Double> subentry : entry.getValue().entrySet()) {
				double tf = (subentry.getValue() * (k + 1))
						/ (k * ((1 - b) + b * (termsNbr.get(subentry.getKey()) / avdl)) + subentry.getValue());

				double idf = Math
						.log10((index.getDocNbr() - entry.getValue().size() + 0.5) / (entry.getValue().size() + 0.5));
				double bm25 = tf * idf;

				tmp.put(subentry.getKey(), bm25);
			}
			weights.put(entry.getKey(), tmp);
		}
		return weights;
	}

	public <T> SortedMap<String, SortedMap<T, Double>> weightLTC(InvertedIndex<T> index) {
		Map<T, Double> norm = new HashMap<>();
		SortedMap<String, SortedMap<T, Double>> weights = new TreeMap<>();
		for (Map.Entry<String, SortedMap<T, Double>> entry : index.getTerms().entrySet()) {
			SortedMap<T, Double> tmp = new TreeMap<>();
			for (Map.Entry<T, Double> subentry : entry.getValue().entrySet()) {
				double tf = 1 + Math.log10((double) subentry.getValue());
				double idf = Math.log10((double) index.getDocNbr() / (double) entry.getValue().size());
				double ltn = tf * idf;
				tmp.put(subentry.getKey(), ltn);
				if (!norm.containsKey(subentry.getKey()))
					norm.put(subentry.getKey(), ltn * ltn);
				else
					norm.put(subentry.getKey(), norm.get(subentry.getKey()) + (ltn * ltn));
			}
			weights.put(entry.getKey(), tmp);
		}
		for (Map.Entry<String, SortedMap<T, Double>> zentry : weights.entrySet()) {
			for (Map.Entry<T, Double> xentry : zentry.getValue().entrySet()) {
				Double tmp = 0.0;
				if (norm.get(xentry.getKey()) != 0)
					tmp = xentry.getValue() / Math.sqrt(norm.get(xentry.getKey()));
				zentry.getValue().put(xentry.getKey(), tmp);
			}
		}
		return weights;
	}

	public <T> SortedMap<String, SortedMap<T, Double>> weightLTN(InvertedIndex<T> index) {
		SortedMap<String, SortedMap<T, Double>> weights = new TreeMap<>();
		for (Map.Entry<String, SortedMap<T, Double>> entry : index.getTerms().entrySet()) {
			SortedMap<T, Double> tmp = new TreeMap<>();
			for (Map.Entry<T, Double> subentry : entry.getValue().entrySet()) {
				double tf = 1 + Math.log10((double) subentry.getValue());
				double idf = Math.log10((double) index.getDocNbr() / (double) entry.getValue().size());
				double ltn = tf * idf;
				tmp.put(subentry.getKey(), ltn);
			}
			weights.put(entry.getKey(), tmp);
		}
		return weights;
	}

}
