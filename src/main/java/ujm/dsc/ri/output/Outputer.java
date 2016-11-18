package ujm.dsc.ri.output;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

@Component
public class Outputer {

	public static final String OUTPUT_FILE = "";
	public static final String TEAM_NAME = "DianaYassineAlhassanHamza";

	public void output(SortedMap<Long, Map<Long, Double>> ranks, int cpt) throws IOException {
		String fileName = "";
		File file = new File("output/" + TEAM_NAME + "_02_0" + cpt + "_ltn_articles_xx.xx.xx.txt");
		int rank = 1;
		FileUtils.write(file, "QueryNumber Q0 Docno Rank Score TeamName Path\n", "UTF-8", false);
		for (Entry<Long, Map<Long, Double>> qentry : ranks.entrySet()) {
			for (Entry<Long, Double> dentry : qentry.getValue().entrySet()) {
				String line = qentry.getKey() + " 0 " + dentry.getKey() + " " + rank + " " + dentry.getValue() + " "
						+ TEAM_NAME + " " + "\\article[1]\n";
				if (rank == 1500)
					rank = 1;
				else {
					rank++;
				}
				FileUtils.write(file, line, "UTF-8", true);
			}
		}
	}

}
