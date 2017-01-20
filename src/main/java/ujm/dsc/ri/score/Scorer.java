package ujm.dsc.ri.score;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class Scorer {
	private static final Logger log = LogManager.getLogger(Scorer.class.getName());

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return -(o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public <T> Map<Long, Map<T, Double>> calculate(SortedMap<String, SortedMap<T, Double>> weights,
			SortedMap<Long, String[]> terms) {
		SortedMap<Long, Map<T, Double>> finals = new TreeMap<>();
		for (Map.Entry<Long, String[]> entry : terms.entrySet()) {
			Map<T, Double> scores = new HashMap<>();
			Long queryId = entry.getKey();
			for (String queryterm : entry.getValue()) {
				SortedMap<T, Double> termWeights = weights.get(queryterm);
				for (Map.Entry<T, Double> subentry : termWeights.entrySet()) {
					if (!scores.containsKey(subentry.getKey()))
						scores.put(subentry.getKey(), subentry.getValue());
					else
						scores.put(subentry.getKey(), scores.get(subentry.getKey()) + subentry.getValue());
				}
			}
			scores = sortByValue(scores);
			Map<T, Double> newScores = new HashMap<>();
			// take the 1st 1500
			int cpt = 1;
			for (Map.Entry<T, Double> nentry : scores.entrySet()) {
				newScores.put(nentry.getKey(), nentry.getValue());
				if (cpt == 1500)
					break;
				else
					cpt++;
			}
			newScores = sortByValue(newScores);
			finals.put(queryId, newScores);
		}
		return finals;
	}

	public <T> SortedMap<Long, Map<T, Double>> calculateZ(SortedMap<String, SortedMap<T, Double>> weights,
			SortedMap<Long, String[]> terms) {
		SortedMap<Long, Map<T, Double>> finals = new TreeMap<>();
		for (Map.Entry<Long, String[]> entry : terms.entrySet()) {
			Map<T, Double> scores = new HashMap<>();
			Long queryId = entry.getKey();
			for (String queryterm : entry.getValue()) {
				if (weights.get(queryterm) == null)
					continue;
				SortedMap<T, Double> termWeights = weights.get(queryterm);

				for (Map.Entry<T, Double> subentry : termWeights.entrySet()) {
					if (!scores.containsKey(subentry.getKey()))
						scores.put(subentry.getKey(), subentry.getValue());
					else
						scores.put(subentry.getKey(), scores.get(subentry.getKey()) + subentry.getValue());
				}
			}
			scores = sortByValue(scores);
			Map<T, Double> newScores = new HashMap<>();
			// take the 1st 1500
			int cpt = 1;
			for (Map.Entry<T, Double> nentry : scores.entrySet()) {
				newScores.put(nentry.getKey(), nentry.getValue());
				if (cpt == 1500)
					break;
				else
					cpt++;
			}
			newScores = sortByValue(newScores);
			finals.put(queryId, newScores);
		}
		return finals;
	}

	//
	public Map<Long, Map<Long, Double>> orderAndTakeBest() {
		return null;
	}

}
