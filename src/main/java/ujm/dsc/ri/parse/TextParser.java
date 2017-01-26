package ujm.dsc.ri.parse;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ujm.dsc.ri.clean.Cleaner;
import ujm.dsc.ri.core.InvertedIndex;

@Component
public class TextParser {

	public static final String DOC_TEXT_PATH = "input/Text_Only_Ascii_Coll_MWI_NoSem";
	private static final Logger log = LogManager.getLogger(TextParser.class.getName());

	private SortedMap<Long, Double> termsNbr = new TreeMap<>();

	@Autowired
	private Cleaner cleaner;

	private String extractDocId(String line) {
		return line.split("<doc><docno>")[1].split("<")[0];
	}

	public Cleaner getCleaner() {
		return cleaner;
	}

	public SortedMap<Long, Double> getTermsNbr() {
		return termsNbr;
	}

	public InvertedIndex<Long> parse(String filePath, Set<String> uniqueTerms) throws IOException {
		log.info("Building inverted index for text file " + filePath);
		long start = System.currentTimeMillis();
		InvertedIndex<Long> index = new InvertedIndex<Long>();
		File file = new File(filePath);
		Long docId = null;
		int cpt = 0;
		LineIterator it = FileUtils.lineIterator(file, "UTF-8");
		try {
			while (it.hasNext()) {
				String line = it.nextLine();
				if (line.trim().startsWith("<doc>")) {
					docId = Long.valueOf(extractDocId(line));
					index.setDocNbr(index.getDocNbr() + 1);
				} else if (!line.trim().startsWith("</doc>")) {
					String[] words = cleaner.cleanLine(StringUtils.split(line));
					cpt += words.length;
					// TODO LEMMA STEM STOP
					for (String term : words) {
						if (uniqueTerms.contains(term))
							index.addTerm(term, docId, 1.0);
					}
				} else { // TODO is this ok ?
					termsNbr.put(docId, (double) cpt);
					cpt = 0;
				}
			}
		} finally {
			LineIterator.closeQuietly(it);
		}
		long end = System.currentTimeMillis();
		log.info("Done in " + (end - start) / 1000.00 + " seconds");
		return index;
	}

	public void setCleaner(Cleaner cleaner) {
		this.cleaner = cleaner;
	}

	public void setTermsNbr(SortedMap<Long, Double> termsNbr) {
		this.termsNbr = termsNbr;
	}

}
