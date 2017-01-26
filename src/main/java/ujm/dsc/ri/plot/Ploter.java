package ujm.dsc.ri.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.springframework.stereotype.Component;

import ujm.dsc.ri.core.Algo;
import ujm.dsc.ri.core.Element;
import ujm.dsc.ri.core.RunStat;
import ujm.dsc.ri.output.Outputer;

@Component
public class Ploter {

	public static final String EVALUATION_FOLDER_PATH = "input/evaluation/";
	public static final String STATS_FOLDER_PATH = "stats/";
	public static final String CSV_OUTPUT_FILE = "runs_stats.csv";
	public static final int WIDTH = 1366;
	public static final int HEIGHT = 768;

	private static DecimalFormat df = new DecimalFormat("##.##"); // TODO ziz :
																	// is this
																	// shit
																	// correct ?

	public String cleanFileName(String fileName, boolean justMyTeam) {
		String cleaned = "";
		int i = 0;
		if (justMyTeam)
			i++;
		String[] tokens = fileName.replaceAll(".i.txt", "").split("_");
		for (; i < tokens.length; i++) {
			cleaned += tokens[i] + "_";
		}
		return cleaned;
	}

	public void drawPrecisionRecall(String runName, SortedMap<Double, Double> precisionRecallData) throws IOException {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries serie = new XYSeries(runName);
		for (Map.Entry<Double, Double> entry : precisionRecallData.entrySet()) {
			serie.add(entry.getKey(), entry.getValue());
		}
		dataset.addSeries(serie);
		JFreeChart lineChartObject = ChartFactory.createXYLineChart("Precision by Recall", "R", "P", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		XYPlot plot = (XYPlot) lineChartObject.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
		plot.getRenderer().setSeriesPaint(0, Color.RED);
		plot.getRenderer().setSeriesStroke(0, new BasicStroke(2));
		/*
		 * Font font = new Font("Dialog", Font.BOLD, 15);
		 * plot.getDomainAxis().setTickLabelFont(font);
		 * plot.getRangeAxis().setTickLabelFont(font);
		 */
		int width = WIDTH;
		int height = HEIGHT;
		File lineChart = new File(STATS_FOLDER_PATH + "/" + runName + ".jpg");
		ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
	}

	public void drawPrecisionRecall(String title, String fileName,
			SortedMap<String, SortedMap<Double, Double>> precisionRecallData) throws IOException {
		XYSeriesCollection dataset = new XYSeriesCollection();
		for (Map.Entry<String, SortedMap<Double, Double>> entry : precisionRecallData.entrySet()) {
			String runName = entry.getKey();
			XYSeries serie = new XYSeries(runName);
			SortedMap<Double, Double> data = entry.getValue();
			for (Map.Entry<Double, Double> subEntry : data.entrySet()) {
				serie.add(subEntry.getKey(), subEntry.getValue());
			}
			dataset.addSeries(serie);
		}
		JFreeChart lineChartObject = ChartFactory.createXYLineChart(title, "R", "P", dataset, PlotOrientation.VERTICAL,
				true, true, false);
		XYPlot plot = (XYPlot) lineChartObject.getPlot();
		plot.setBackgroundPaint(Color.WHITE);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
		/*
		 * plot.getRenderer().setSeriesPaint(0, Color.RED);
		 * plot.getRenderer().setSeriesStroke(0, new BasicStroke(2)); Font font
		 * = new Font("Dialog", Font.BOLD, 15);
		 * plot.getDomainAxis().setTickLabelFont(font);
		 * plot.getRangeAxis().setTickLabelFont(font);
		 */
		int width = WIDTH;
		int height = HEIGHT;
		File lineChart = new File(STATS_FOLDER_PATH + "/" + fileName + ".jpg");
		ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
	}

	public void plotAllPrecisionByRecall(boolean justMyTeam) throws IOException {
		df.setRoundingMode(RoundingMode.HALF_EVEN);
		File[] runs = new File(EVALUATION_FOLDER_PATH).listFiles();
		List<RunStat> runStats = new ArrayList<>();
		for (File run : runs) {
			if (run.isFile() && run.getName().contains(Outputer.TEAM_NAME) == justMyTeam) {
				RunStat runStat = new RunStat();
				SortedMap<Double, Double> precisionRecallData = new TreeMap<>();
				List<String> lines = FileUtils.readLines(run, "UTF-8");
				String runName = cleanFileName(run.getName(), justMyTeam);
				runStat.setRunName(runName);
				double magp = Double.valueOf(lines.get(155).replaceAll("\\s+", " ").split(" ")[2]);
				double magpRouned = Double.valueOf(df.format(magp * 100));
				runStat.setMagp(magpRouned);
				double num_ret = Double.valueOf(lines.get(149).replaceAll("\\s+", " ").split(" ")[2]);
				double num_rel = Double.valueOf(lines.get(150).replaceAll("\\s+", " ").split(" ")[2]);
				double num_rel_ret = Double.valueOf(lines.get(151).replaceAll("\\s+", " ").split(" ")[2]);
				double recall = num_rel_ret / num_rel;
				runStat.setRecall(Double.valueOf(df.format(recall * 100)));
				double precision = num_rel_ret / num_ret;
				runStat.setPrecision(Double.valueOf(df.format(precision * 100)));
				for (int i = 170; i < 181; i++) {
					String[] tmp = lines.get(i).replaceAll("\\s+", " ").split("ircl_prn.")[1].split(" ");
					double x = Double.valueOf(tmp[0].replaceAll(",", "."));
					double y = Double.valueOf(tmp[2]);
					precisionRecallData.put(x, y);
				}
				drawPrecisionRecall(runName, precisionRecallData);
				runStats.add(runStat);
			}
		}
		writeCSV(runStats);
	}

	public void plotPrecisionByRecallGrouped(Algo algo, Element elem, String title, String outputfileName,
			boolean justMyTeam) throws IOException {
		File[] files = new File(EVALUATION_FOLDER_PATH).listFiles();
		SortedMap<String, SortedMap<Double, Double>> precisionRecallData = new TreeMap<>();
		loop: for (File file : files) {
			if (file.isFile() && file.getName().contains(Outputer.TEAM_NAME) == justMyTeam
					&& file.getName().contains(algo.toString())) {
				if (!elem.equals(Element.all)) {
					if (!file.getName().contains(elem.toString())) {
						continue loop;
					}
				}
				if (algo.equals(Algo.bm25)) {
					if (file.getName().contains(Algo.bm25fa.toString())
							|| file.getName().contains(Algo.bm25fp.toString())) {
						continue loop;
					}
				}
				List<String> lines = FileUtils.readLines(file, "UTF-8");
				SortedMap<Double, Double> data = new TreeMap<>();
				String runName = cleanFileName(file.getName(), justMyTeam);
				for (int i = 170; i < 181; i++) {
					String[] tmp = lines.get(i).replaceAll("\\s+", " ").split("ircl_prn.")[1].split(" ");
					double x = Double.valueOf(tmp[0].replaceAll(",", "."));
					double y = Double.valueOf(tmp[2]);
					data.put(x, y);
				}
				precisionRecallData.put(runName, data);
			}
		}
		drawPrecisionRecall(title, outputfileName, precisionRecallData);
	}

	public void plotSpecificPrecisionByRecallGrouped(String[] runs, String title, String outputfileName,
			boolean justMyTeam) throws IOException {
		List<String> runsL = Arrays.asList(runs);
		File[] files = new File(EVALUATION_FOLDER_PATH).listFiles();
		SortedMap<String, SortedMap<Double, Double>> precisionRecallData = new TreeMap<>();
		for (File file : files) {
			if (file.isFile() && file.getName().contains(Outputer.TEAM_NAME) == justMyTeam) {
				for (String run : runsL) {
					if (file.getName().contains(run)) {
						SortedMap<Double, Double> data = new TreeMap<>();
						List<String> lines = FileUtils.readLines(file, "UTF-8");
						String runName = cleanFileName(file.getName(), justMyTeam);
						for (int i = 170; i < 181; i++) {
							String[] tmp = lines.get(i).replaceAll("\\s+", " ").split("ircl_prn.")[1].split(" ");
							double x = Double.valueOf(tmp[0].replaceAll(",", "."));
							double y = Double.valueOf(tmp[2]);
							data.put(x, y);
						}
						precisionRecallData.put(runName, data);
					}
				}
			}
		}
		drawPrecisionRecall(title, outputfileName, precisionRecallData);
	}

	public void writeCSV(List<RunStat> runStats) {
		FileWriter fileWriter = null;
		CSVPrinter csvFilePrinter = null;
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n").withDelimiter(';');
		Object[] header = { "RUN_NAME", "MAGP", "RECALL", "PRECISION" };
		try {
			fileWriter = new FileWriter(STATS_FOLDER_PATH + "/" + CSV_OUTPUT_FILE, false);
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
			csvFilePrinter.printRecord(header);
			for (RunStat runStat : runStats) {
				List<String> dataRecord = new ArrayList<>();
				dataRecord.add(runStat.getRunName());
				dataRecord.add(runStat.getMagp().toString());
				dataRecord.add(runStat.getRecall().toString());
				dataRecord.add(runStat.getPrecision().toString());
				csvFilePrinter.printRecord(dataRecord);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				csvFilePrinter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
