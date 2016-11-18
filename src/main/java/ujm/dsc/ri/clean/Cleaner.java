package ujm.dsc.ri.clean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class Cleaner {

	public String extractDocId(String line) {
		return line.split("<doc><docno>")[1].split("<")[0];
	}

	public String cleanWord(String word) {
		if (StringUtils.isNumeric(word) || word.length() <= 1 || word.toLowerCase().startsWith("http"))
			return "";
		else // TODO trim and to lowercase not here
			return word.trim().toLowerCase().replaceAll("[^a-zA-Z]", "");
	}

	public String[] cleanLine(String[] words) {
		List<String> wordList = new ArrayList<>();
		List<String> cleanedList = new ArrayList<>();
		Collections.addAll(wordList, words);
		for (String word : wordList) {
			cleanedList.add(this.cleanWord(word));
		}
		words = cleanedList.toArray(new String[cleanedList.size()]);
		return words;
	}

}
