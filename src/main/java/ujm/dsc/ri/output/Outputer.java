package ujm.dsc.ri.output;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class Outputer {

	public static final String OUTPUT_FOLDER = "output/";
	public static final String TMP_OUTPUT_FOLDER = OUTPUT_FOLDER + "tmp/";
	public static final String TEAM_NAME = "DianaYassineAlhassanHamza";

	private void mergefiles() throws IOException {
		List<File> files = (List<File>) FileUtils.listFiles(new File(TMP_OUTPUT_FOLDER), TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);
		for (File file : files) {
			List<String> lines = FileUtils.readLines(file, "UTF-8");
			FileUtils.writeLines(new File(OUTPUT_FOLDER + file.getName().split("#")[0] + ".txt"), lines, true);
			FileUtils.deleteQuietly(file);
		}
	}

	public <T> void output(Map<Long, Map<T, Double>> ranks, String step, String run, String algo, String k, String b,
			String atitle, String acategory, String aabstract, String acontent) throws IOException {
		String outputPath = "";
		if (algo.equals("bm25"))
			outputPath = OUTPUT_FOLDER + TEAM_NAME + "_" + step + "_" + run + "_" + algo + "_articles.k" + k + "b" + b
					+ ".txt";
		else if (algo.startsWith("bm25f"))
			outputPath = OUTPUT_FOLDER + TEAM_NAME + "_" + step + "_" + run + "_" + algo + "_articles.k" + k + "b" + b
					+ "atitle" + atitle + "acategory" + acategory + "aabstract" + aabstract + "acontent" + acontent
					+ ".txt";
		else
			outputPath = OUTPUT_FOLDER + TEAM_NAME + "_" + step + "_" + run + "_" + algo + "_articles.txt";
		File file = new File(outputPath);
		int rank = 1;
		for (Entry<Long, Map<T, Double>> qentry : ranks.entrySet()) {
			for (Entry<T, Double> dentry : qentry.getValue().entrySet()) {
				String line = qentry.getKey() + " 0 " + dentry.getKey() + " " + rank + " " + dentry.getValue() + " "
						+ TEAM_NAME + " " + "/article[1]\n";
				if (rank == 1500)
					rank = 1;
				else {
					rank++;
				}
				FileUtils.write(file, line, "UTF-8", true);
			}
		}
	}

	public void outputK(Map<Long, Map<String, Double>> ranks, String step, String run, String algo, String element,
			String k, String b) throws IOException {
		for (Entry<Long, Map<String, Double>> qentry : ranks.entrySet()) {
			String outputPath = OUTPUT_FOLDER + TEAM_NAME + "_" + step + "_" + run + "_" + algo + "_" + element + ".k"
					+ k + "b" + b + ".txt";
			if (!algo.equals("bm25"))
				outputPath = OUTPUT_FOLDER + TEAM_NAME + "_" + step + "_" + run + "_" + algo + "_" + element + ".txt";
			int rank = 1;
			for (Entry<String, Double> dentry : qentry.getValue().entrySet()) {
				File file = new File(outputPath);
				String line = qentry.getKey() + " 0 " + dentry.getKey().split("\\?")[0] + " " + rank + " "
						+ dentry.getValue() + " " + TEAM_NAME + " "
						+ StringUtils.substring(dentry.getKey().split("\\?")[1], 1) + "\n";
				rank = (rank == 1500) ? 1 : rank + 1;
				FileUtils.write(file, line, "UTF-8", true);
			}
		}
	}

	public void outputU(Map<Long, Map<String, Double>> ranks, String step, String run, String algo, String element,
			String k, String b) throws IOException {
		int cpt = 1;
		for (Entry<Long, Map<String, Double>> qentry : ranks.entrySet()) {
			String outputPath = TMP_OUTPUT_FOLDER + TEAM_NAME + "_" + step + "_" + run + "_" + algo + "_" + element
					+ ".k" + k + "b" + b + "#" + cpt;
			if (!algo.equals("bm25"))
				outputPath = TMP_OUTPUT_FOLDER + TEAM_NAME + "_" + step + "_" + run + "_" + algo + "_" + element + "#"
						+ cpt;
			cpt++;
			int rank = 1;
			for (Entry<String, Double> dentry : qentry.getValue().entrySet()) {
				File file = new File(outputPath);
				String line = qentry.getKey() + " 0 " + dentry.getKey().split("\\?")[0] + " " + rank + " "
						+ dentry.getValue() + " " + TEAM_NAME + " "
						+ StringUtils.substring(dentry.getKey().split("\\?")[1], 1) + "\n";
				rank = (rank == 1500) ? 1 : rank + 1;
				FileUtils.write(file, line, "UTF-8", true);
			}
		}
		this.postProcessU();
		this.mergefiles();
	}

	private void postProcessU() throws IOException {
		List<File> files = (List<File>) FileUtils.listFiles(new File(TMP_OUTPUT_FOLDER), TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);
		for (File file : files) {
			List<String> lines = FileUtils.readLines(file, "UTF-8");
			outerloop: for (int i = 0; i < lines.size(); i++) {
				if (lines.get(i) == null)
					continue outerloop;
				FileUtils.writeStringToFile(new File(TMP_OUTPUT_FOLDER + file.getName() + "_"), lines.get(i) + "\n",
						"UTF-8", true);
				innerloop: for (int j = i + 1; j < lines.size(); j++) {
					if (lines.get(j) == null)
						continue innerloop;
					if ((lines.get(j).split(" ")[2]).trim().toLowerCase()
							.equalsIgnoreCase(((lines.get(i).split(" ")[2]).toLowerCase().trim()))) {
						FileUtils.writeStringToFile(new File(TMP_OUTPUT_FOLDER + file.getName() + "_"),
								lines.get(j) + "\n", "UTF-8", true);
						lines.set(j, null);
					}
				}
			}
			FileUtils.deleteQuietly(file);
		}
	}

}
