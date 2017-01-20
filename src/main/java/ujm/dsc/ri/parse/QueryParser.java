package ujm.dsc.ri.parse;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import ujm.dsc.ri.core.QueryCollection;

@Component
public class QueryParser {

	public static final String QUERY_PATH = "input/topics_M2DSC7Q_2016_17.txt";
	public static final String TEST_QUERY_PATH = "input/query.txt";
	private static final Logger log = LogManager.getLogger(QueryParser.class.getName());

	public QueryCollection parse(String filePath) throws IOException {
		log.info("Building inverted indexing for query file " + filePath);
		long start = System.currentTimeMillis();
		QueryCollection queries = new QueryCollection();
		File file = new File(filePath);
		Long queryId = null;
		LineIterator it = FileUtils.lineIterator(file, "UTF-8");
		try {
			while (it.hasNext()) {
				String line = it.nextLine();
				String[] tokens = StringUtils.split(line);
				queryId = Long.valueOf(tokens[0]);
				String[] terms = Arrays.copyOfRange(tokens, 1, tokens.length);
				queries.addTerm(queryId, terms);
			}
		} finally {
			LineIterator.closeQuietly(it);
		}
		long end = System.currentTimeMillis();
		log.info("Done in " + (end - start) / 1000.00 + " seconds");
		return queries;
	}

}
