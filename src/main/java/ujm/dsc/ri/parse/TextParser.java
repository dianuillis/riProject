package ujm.dsc.ri.parse;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ujm.dsc.ri.clean.Cleaner;
import ujm.dsc.ri.core.Doc;
import ujm.dsc.ri.core.DocumentsCollection;

@Component
public class TextParser {

	private static final Logger log = LogManager.getLogger(TextParser.class.getName());

	public static final String DOC_TEXT_PATH = "input/Text_Only_Ascii_Coll_MWI_NoSem";

	@Autowired
	private Cleaner cleaner = new Cleaner();

	public DocumentsCollection parseTextFile(String filePath) throws IOException {
		log.info("Indexing text file " + filePath);
		long start = System.currentTimeMillis();
		DocumentsCollection collection = new DocumentsCollection();
		Doc document = new Doc();
		File file = new File(DOC_TEXT_PATH);
		LineIterator it = FileUtils.lineIterator(file, "UTF-8");
		try {
			while (it.hasNext()) {
				String line = it.nextLine();
				if (line.trim().startsWith("<doc>")) {
					Long id = Long.valueOf(cleaner.extractDocId(line));
					document.setId(id);
				} else if (line.trim().startsWith("</doc>")) {
					collection.addDocument(document);
					// if (collection.getDocuments().size() == 1500)
					// return collection;
					document = new Doc();
					// System.out.println("done > " + step++);
				} else {
					String[] words = cleaner.cleanLine(StringUtils.split(line));
					for (String word : words) {
						document.addTerm(cleaner.cleanWord(word));
					}
				}
			}
		} finally {
			LineIterator.closeQuietly(it);
		}
		long end = System.currentTimeMillis();
		log.info("Done in " + (end - start) / 1000.00 + " seconds");
		return collection;
	}

}
