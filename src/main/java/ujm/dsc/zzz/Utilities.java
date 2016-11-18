package ujm.dsc.zzz;

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

public class Utilities {

	static Map<Term, Map<Document, Integer>> invertedIndex = new TreeMap<Term, Map<Document, Integer>>();

	public void createPosting() throws IOException {

		System.out.println("Start create posting");

		Long start = System.currentTimeMillis();

		String docId = "";

		File file = new File("docs/doctest");

		LineIterator it = FileUtils.lineIterator(file, "UTF-8");

		try {
			while (it.hasNext()) {

				String line = it.nextLine();
				// if we reach a new document, we change the id
				if (line.trim().startsWith("<doc>")) {
					docId = extractDocId(line);
					System.out.println("DocID " + docId);
				} else {
					String[] words = StringUtils.split(line);
					String[] cleaned = cleanLine(words);
					buildIndex(cleaned, new Document(Integer.parseInt(docId)));
				}
			}
		} finally {
			LineIterator.closeQuietly(it);
		}
		// tofile(invertedIndex);
		invertedIndex.clear();
		// fromfile("index.ser");
		printIndex();

		Long end = System.currentTimeMillis();

		System.out.println("*** Completed in : " + (end - start) / 1000 + " s ***");
	}

	private void printIndex() {

		System.out.println("Print invertedIndex");

		for (Entry<Term, Map<Document, Integer>> entry : invertedIndex.entrySet()) {
			Term term = entry.getKey();
			Map<Document, Integer> postings = entry.getValue();
			System.out.print("Term " + term.getId() + " DF " + term.getDf() + " IDF " + term.getIdf());

		}

	}

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
	 * building the inverted index, line by line, TODO explain more
	 */
	public void buildIndex(String[] words, Document document) {
		// get the docId

		for (String word : words) {

			if (invertedIndex.containsKey(new Term(word))) {

				Map<Document, Integer> tmpMap = invertedIndex.get(new Term(word));
				if (tmpMap.containsKey(document)) {

					tmpMap.put(document, tmpMap.get(document) + 1);

					invertedIndex.put(new Term(word), tmpMap);
				} else {
					tmpMap.put(document, 1);
					invertedIndex.put(new Term(word), tmpMap);
				}
			} else {
				SortedMap<Document, Integer> auxMap = new TreeMap<Document, Integer>();

				auxMap.put(document, 1);
				invertedIndex.put(new Term(word), auxMap);
			}
		}

	}

	public Map<Term, Map<Document, Integer>> getInvertedIndex() {
		return invertedIndex;
	}

	public void setInvertedIndex(Map<Term, Map<Document, Integer>> invertedIndex) {
		Utilities.invertedIndex = invertedIndex;
	}

}
