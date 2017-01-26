package ujm.dsc.ri.clean;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class Cleaner {

	public String[] cleanLine(String[] words) {
		List<String> wordList = new ArrayList<>();
		List<String> cleanedList = new ArrayList<>();
		Collections.addAll(wordList, words);
		for (String word : wordList)
			cleanedList.add(this.cleanWord(word));
		words = cleanedList.toArray(new String[cleanedList.size()]);
		return words;
	}

	public void cleanOutputFolder(String folderPath) throws IOException {
		FileUtils.deleteDirectory(new File(folderPath));
	}

	public String cleanWord(String word) {
		if (StringUtils.isNumeric(word) || word.length() <= 1 || word.toLowerCase().startsWith("http"))
			return "";
		else // TODO trim and to lowercase not here
			return word.trim().toLowerCase().replaceAll("[^a-zA-Z]", "");
	}

}
