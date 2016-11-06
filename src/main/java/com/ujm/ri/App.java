package com.ujm.ri;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

class Posting {

	private String docId;
	private int tf;

	public Posting(String docId, int tf) {
		super();
		this.docId = docId;
		this.tf = tf;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public int getTf() {
		return tf;
	}

	public void setTf(int tf) {
		this.tf = tf;
	}
}

public class App {

	private static SortedMap<String, Integer> map = new TreeMap<>();
	private static SortedMap<String, SortedMap<String, Integer>> bigMap = new TreeMap<>();
	private static SortedMap<String, SortedMap<Integer, Integer>> finalMap = new TreeMap<>();

	/*
	 * TODO
	 */
	public static String extractDocId(String line) {
		String[] splits = line.split("<doc><docno>");
		String prevDocId = splits[1];
		String docId = prevDocId.split("<")[0];
		return docId;
	}

	public static String cleanWord(String word) {
		/*
		 * String[] endings = { ",", ".", ":", ")", "(", ";" }; for (String tmp
		 * : endings) { if (word.endsWith(tmp)) { word =
		 * StringUtils.removeEnd(word, tmp); break; } }
		 */
		if (StringUtils.containsAny("1234567890", word))
			return "";
		else
			return word.replaceAll("[^a-zA-Z^]", "");
	}

	public static String[] cleanLine(String[] words) {
		List<String> wordList = new ArrayList<String>();
		List<String> cleanList = new ArrayList<>();
		Collections.addAll(wordList, words);
		for (String word : wordList) {
			String tmp = cleanWord(word);
			if (tmp.length() > 1 && !tmp.startsWith("http")) {
				cleanList.add(tmp.toLowerCase());
			}
		}
		words = cleanList.toArray(new String[cleanList.size()]);
		return words;
	}

	public static void fillDictionary(String[] words) {
		for (String word : words) {
			if (map.containsKey(word)) {
				map.put(word, map.get(word) + 1);
			} else
				map.put(word, 1);
		}
	}

	public static void betterFillDictionary(String[] words, String doc) {
		Integer docId = Integer.parseInt(doc);
		for (String word : words) {
			if (finalMap.containsKey(word)) {
				SortedMap<Integer, Integer> tmpMap = finalMap.get(word);
				if (tmpMap.containsKey(docId)) {
					tmpMap.put(docId, tmpMap.get(docId) + 1);
					finalMap.put(word, tmpMap);
				} else {
					tmpMap.put(docId, 1);
					finalMap.put(word, tmpMap);
				}

			} else {
				SortedMap<Integer, Integer> auxMap = new TreeMap<>();
				auxMap.put(docId, 1);
				finalMap.put(word, auxMap);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		Long start = System.currentTimeMillis();
		String docId = "";
		File file = new File("doc/Text_Only_Ascii_Coll_MWI_NoSem");
		// List<String> lines = FileUtils.readLines(file, "UTF-8");
		// System.out.println(">>>" + lines.size());
		LineIterator it = FileUtils.lineIterator(file, "UTF-8");
		try {
			while (it.hasNext()) {
				String line = it.nextLine();
				if (line.trim().startsWith("<doc>")) {
					docId = extractDocId(line);
				} else {
					String[] words = StringUtils.split(line);
					String[] cleaned = cleanLine(words);
					betterFillDictionary(cleaned, docId);
					// bigMap.put(docId, map);
				}
				if (line.trim().equalsIgnoreCase("</doc>")) {
					// map = new TreeMap<>();
					// docId = "";
				}
			}
		} finally {
			LineIterator.closeQuietly(it);
		}
		/*
		 * TODO printing bigMap
		 */
		/*
		 * for (Entry<String, SortedMap<String, Integer>> entry :
		 * bigMap.entrySet()) { String id = entry.getKey(); SortedMap<String,
		 * Integer> littleMap = entry.getValue();
		 * System.out.println("--------------------------------------------");
		 * System.out.println("docId : " + id); for (Entry<String, Integer>
		 * littleEntry : littleMap.entrySet()) {
		 * System.out.println(littleEntry.getKey() + " : " +
		 * littleEntry.getValue()); } }
		 */

		for (Entry<String, SortedMap<Integer, Integer>> entry : finalMap.entrySet()) {
			String term = entry.getKey();
			SortedMap<Integer, Integer> postings = entry.getValue();
			System.out.print(term + ":\t");
			for (Entry<Integer, Integer> littleEntry : postings.entrySet()) {
				System.out.print(littleEntry.getKey() + ":" + littleEntry.getValue() + ";");
			}
			System.out.println();
		}
		Long end = System.currentTimeMillis();
		System.out.println("Competed in : " + (end - start) / 1000 + " s");
	}
}
