package com.ujm.ri;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;

public class App {

	private static SortedMap<String, SortedMap<Integer, Integer>> invertedIndex = new TreeMap<>();

	/*
	 * extracting the id of each document
	 */
	public static String extractDocId(String line) {
		String[] splits = line.split("<doc><docno>");
		String prevDocId = splits[1];
		String docId = prevDocId.split("<")[0];
		return docId;
	}

	/*
	 * cleaning a token
	 */
	public static String cleanWord(String word) {
		// if the token contains a digit, removes it
		if (StringUtils.isNumeric(word))
			return "";
		// else we remove all non alpha characters
		else
			return word.replaceAll("[^a-zA-Z]", "");
	}

	/*
	 * cleaning a line (array of tokens)
	 */
	public static String[] cleanLine(String[] words) {
		List<String> wordList = new ArrayList<>();
		List<String> cleanList = new ArrayList<>();
		Collections.addAll(wordList, words);
		for (String word : wordList) {
			String tmp = cleanWord(word);
			// if a token is of size <= 1 or start with http (url) we do not
			// consider it
			if (tmp.length() > 1 && !tmp.startsWith("http")) {
				cleanList.add(tmp.toLowerCase());
			}
		}
		words = cleanList.toArray(new String[cleanList.size()]);
		return words;
	}

	/*
	 * building the inverted index, line by line, TODO explain more
	 */
	public static void buildIndex(String[] words, String doc) {
		// get the docId
		Integer docId = Integer.parseInt(doc);
		for (String word : words) {
			if (invertedIndex.containsKey(word)) {
				SortedMap<Integer, Integer> tmpMap = invertedIndex.get(word);
				if (tmpMap.containsKey(docId)) {
					tmpMap.put(docId, tmpMap.get(docId) + 1);
					invertedIndex.put(word, tmpMap);
				} else {
					tmpMap.put(docId, 1);
					invertedIndex.put(word, tmpMap);
				}
			} else {
				SortedMap<Integer, Integer> auxMap = new TreeMap<>();
				auxMap.put(docId, 1);
				invertedIndex.put(word, auxMap);
			}
		}
	}

	/*
	 * printing the index
	 */
	public static void printIndex() {
		for (Entry<String, SortedMap<Integer, Integer>> entry : invertedIndex.entrySet()) {
			String term = entry.getKey();
			SortedMap<Integer, Integer> postings = entry.getValue();
			System.out.print(term + " [");
			for (Entry<Integer, Integer> littleEntry : postings.entrySet()) {
				System.out.print(littleEntry.getKey() + ":" + littleEntry.getValue() + ";");
			}
			System.out.println("]");
		}
	}

	public static void main(String[] args) throws IOException {

		Long start = System.currentTimeMillis();

		String docId = "";

		File file = new File("doc/Text_Only_Ascii_Coll_MWI_NoSem");

		LineIterator it = FileUtils.lineIterator(file, "UTF-8");
		try {
			while (it.hasNext()) {
				String line = it.nextLine();
				// if we reach a new document, we change the id
				if (line.trim().startsWith("<doc>")) {
					docId = extractDocId(line);
				} else {
					String[] words = StringUtils.split(line);
					String[] cleaned = cleanLine(words);
					buildIndex(cleaned, docId);
				}
			}
		} finally {
			LineIterator.closeQuietly(it);
		}

		printIndex();

		Long end = System.currentTimeMillis();

		System.out.println("*** Completed in : " + (end - start) / 1000 + " s ***");
	}
}
