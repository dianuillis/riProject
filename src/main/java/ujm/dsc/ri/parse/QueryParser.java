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
import ujm.dsc.ri.core.QueriesCollection;
import ujm.dsc.ri.core.Query;

@Component
public class QueryParser {

	private static final Logger log = LogManager.getLogger(QueryParser.class.getName());

	public static final String QUERY_PATH = "input/topics_M2DSC7Q_2016_17.txt";

	@Autowired
	private Cleaner cleaner;

	public QueriesCollection parseQueryFile(String filePath) throws IOException {
		log.info("Indexing query " + filePath);
		long start = System.currentTimeMillis();
		QueriesCollection collection = new QueriesCollection();
		Query query = new Query();
		File file = new File(QUERY_PATH);
		LineIterator it = FileUtils.lineIterator(file, "UTF-8");
		try {
			while (it.hasNext()) {
				String line = it.nextLine();
				String[] tokens = StringUtils.split(line);
				query.setId(Long.valueOf(tokens[0]));
				for (int i = 1; i < tokens.length; i++) {
					query.addTerm(cleaner.cleanWord(tokens[i]));
				}
				collection.addQuery(query);
				query = new Query();
			}
		} finally {
			LineIterator.closeQuietly(it);
		}
		long end = System.currentTimeMillis();
		log.info("Done in " + (end - start) / 1000.00 + " seconds");
		return collection;
	}

}
