package ujm.dsc.ri.score;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class Scorer {

	private static final Logger log = LogManager.getLogger(Scorer.class.getName());

	public SortedMap<Long, Map<Long, Double>> scoreDocsLtn(SortedMap<String, SortedMap<Long, Double>> docWeights,
			SortedMap<String, SortedMap<Long, Double>> queryWeights) {
		log.info("Calculating documents scores");
		int cpt = 0;
		long start = System.currentTimeMillis();
		SortedMap<Long, Map<Long, Double>> scores = new TreeMap<>();
		for (Entry<String, SortedMap<Long, Double>> qentry : queryWeights.entrySet()) {
			String qterm = qentry.getKey();
			for (Entry<Long, Double> sqentry : qentry.getValue().entrySet()) {
				for (Entry<String, SortedMap<Long, Double>> dentry : docWeights.entrySet()) {
					String dterm = dentry.getKey();
					for (Entry<Long, Double> sdentry : dentry.getValue().entrySet()) {
						Long docId = sdentry.getKey();
						Long queryId = sqentry.getKey();
						Double newScore = sdentry.getValue() * sqentry.getValue();
						Map<Long, Double> map = new HashMap<>();
						if (!scores.containsKey(queryId)) {
							map.put(docId, newScore);
						} else {
							map = scores.get(queryId);
							// System.out.println(map);
							if (!map.containsKey(docId)) {
								map.put(docId, newScore);
							} else {
								double oldScore = map.get(docId);
								oldScore = oldScore + newScore;
								map.put(docId, oldScore);
							}
						}
						map = sortByValue(map);
						scores.put(queryId, map);
					}
				}
			}
		}
		System.out.println(scores);
		long end = System.currentTimeMillis();
		log.info("Done in " + (end - start) / 1000.00 + " seconds");
		return scores;
	}

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
}
