package ujm.dsc.ri.shell;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import ujm.dsc.ri.clean.Cleaner;
import ujm.dsc.ri.core.InvertedIndex;
import ujm.dsc.ri.core.QueryCollection;
import ujm.dsc.ri.output.Outputer;
import ujm.dsc.ri.parse.QueryParser;
import ujm.dsc.ri.parse.TextParser;
import ujm.dsc.ri.parse.XmlParser;
import ujm.dsc.ri.score.Scorer;
import ujm.dsc.ri.weight.Weighter;

@Component
public class RISCommands implements CommandMarker {

	public static final String RUNS_PARAMS_FILE = "input/runs.txt";

	private static final String ERROR_MESSAGE = "not supported";

	@Autowired
	private TextParser textParser;

	@Autowired
	private QueryParser queryParser;

	@Autowired
	private XmlParser xmlParser;

	@Autowired
	private Weighter weighter;

	@Autowired
	private Scorer scorer;

	@Autowired
	private Outputer outputer;

	@Autowired
	private Cleaner cleaner;

	@CliCommand(value = "about", help = "app creators")
	public String about() {
		return "BAAZIZ Hamza, BAH Alhassan, BENDARI Yassine, RAMIREZ Diana - M2 DSC - UJM 2016/17";
	}

	@CliAvailabilityIndicator({ "!", "//", ":", "date", "script", "system properties", "version" })
	public boolean isDefaultAvailable() {
		return false;
	}

	@CliCommand(value = "run", help = "to define")
	public String run() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {

		// remove old runs
		cleaner.cleanOutputFolder(Cleaner.OUTPUT_FOLDER_PATH);

		// inits
		InvertedIndex<Long> textInvertedIndex = null;
		InvertedIndex<Long> xmlInvertedIndex = null;
		InvertedIndex<Long> xmlInvertedIndexF = null;
		InvertedIndex<String> secInvertedIndex = null;
		InvertedIndex<String> pInvertedIndex = null;
		QueryCollection queries = queryParser.parse(QueryParser.QUERY_PATH);
		SortedMap<String, SortedMap<Long, Double>> weights;
		SortedMap<String, SortedMap<String, Double>> secWeights = null;
		SortedMap<String, SortedMap<String, Double>> pWeights = null;
		Map<Long, Map<Long, Double>> scores = null;
		Map<Long, Map<String, Double>> secScores = null;
		Map<Long, Map<String, Double>> pScores = null;
		Map<String, InvertedIndex<Long>> indexes = null;
		SortedMap<String, SortedMap<Long, Double>> titleWeights, categoryWeights, abstractWeights, contentWeights;
		SortedMap<Long, Map<Long, Double>> titleScores, categoryScores, abstractScores, contentScores;

		// opt1: which index we need to build ?
		Set<String> indexesToBuild = new HashSet<>();

		List<String> lines = FileUtils.readLines(new File(RUNS_PARAMS_FILE), "UTF-8");

		// 1st pass for opt1
		for (String line : lines) {
			if (!line.startsWith("#")) {
				String input = line.split(" ")[2];
				String type = line.split(" ")[3];
				if (input.equals("text"))
					indexesToBuild.add("text");
				if (input.equals("xml"))
					indexesToBuild.add("xml");
				if (type.equals("sec"))
					indexesToBuild.add("sec");
				if (type.equals("p"))
					indexesToBuild.add("p");
			}
		}

		//
		if (indexesToBuild.isEmpty())
			return ERROR_MESSAGE;

		// indexing based on opt1
		if (indexesToBuild.contains("text"))
			textInvertedIndex = textParser.parse(TextParser.DOC_TEXT_PATH, queries.getUniqueTerms());
		if (indexesToBuild.contains("xml")) {
			xmlInvertedIndex = xmlParser.parse(XmlParser.XML_FOLDER_PATH, queries.getUniqueTerms());
		}
		if (indexesToBuild.contains("sec"))
			secInvertedIndex = xmlParser.parseU(XmlParser.XML_FOLDER_PATH, queries.getUniqueTerms());
		if (indexesToBuild.contains("p"))
			pInvertedIndex = xmlParser.parseK(XmlParser.XML_FOLDER_PATH, queries.getUniqueTerms());

		// heavy shit
		for (String line : lines) {
			if (!line.startsWith("#")) {
				String[] params = line.split(" ");
				if (params[3].equals("article")) {
					if (params[2].equals("text")) {
						switch (params[4]) {
						case "ltn":
							weights = weighter.weightLTN(textInvertedIndex);
							break;
						case "ltc":
							weights = weighter.weightLTC(textInvertedIndex);
							break;
						case "bm25":
							weights = weighter.weightBM25(textInvertedIndex, textParser.getTermsNbr(),
									Double.valueOf(params[5]), Double.valueOf(params[6]));
							break;
						default:
							return ERROR_MESSAGE;
						}
						scores = scorer.calculate(weights, queries.getTerms());
						outputer.output(scores, params[0], params[1], params[4], params[5], params[6], "", "", "", "");
					} else if (params[2].equals("xml")) {
						switch (params[4]) {
						case "ltn":
							weights = weighter.weightLTN(xmlInvertedIndex);
							scores = scorer.calculate(weights, queries.getTerms());
							break;
						case "ltc":
							weights = weighter.weightLTC(xmlInvertedIndex);
							scores = scorer.calculate(weights, queries.getTerms());
							break;
						case "bm25":
							weights = weighter.weightBM25(xmlInvertedIndex, xmlParser.getTermsNbr(),
									Double.valueOf(params[5]), Double.valueOf(params[6]));
							scores = scorer.calculate(weights, queries.getTerms());
						case "bm25fa":
							Map<String, Double> alphas = new HashMap<>();
							alphas.put("title", Double.valueOf(params[7]));
							alphas.put("category", Double.valueOf(params[8]));
							alphas.put("abstract", Double.valueOf(params[9]));
							alphas.put("content", Double.valueOf(params[10]));
							xmlInvertedIndexF = xmlParser.parseF(XmlParser.XML_FOLDER_PATH, queries.getUniqueTerms(),
									alphas);
							weights = weighter.weightBM25(xmlInvertedIndexF, xmlParser.getTermsNbr(),
									Double.valueOf(params[5]), Double.valueOf(params[6]));
							scores = scorer.calculate(weights, queries.getTerms());
							break;
						case "bm25fp":

							alphas = new HashMap<>();
							alphas.put("title", Double.valueOf(params[7]));
							alphas.put("category", Double.valueOf(params[8]));
							alphas.put("abstract", Double.valueOf(params[9]));
							alphas.put("content", Double.valueOf(params[10]));

							indexes = xmlParser.parseZ(XmlParser.XML_FOLDER_PATH, queries.getUniqueTerms(), alphas);

							titleWeights = weighter.weightBM25(indexes.get("title"),
									xmlParser.getTermsNbrF().get("title"), Double.valueOf(params[5]),
									Double.valueOf(params[6]));
							categoryWeights = weighter.weightBM25(indexes.get("category"),
									xmlParser.getTermsNbrF().get("category"), Double.valueOf(params[5]),
									Double.valueOf(params[6]));
							abstractWeights = weighter.weightBM25(indexes.get("abstract"),
									xmlParser.getTermsNbrF().get("abstract"), Double.valueOf(params[5]),
									Double.valueOf(params[6]));
							contentWeights = weighter.weightBM25(indexes.get("content"),
									xmlParser.getTermsNbrF().get("content"), Double.valueOf(params[5]),
									Double.valueOf(params[6]));

							titleScores = scorer.calculateZ(titleWeights, queries.getTerms());
							categoryScores = scorer.calculateZ(categoryWeights, queries.getTerms());
							abstractScores = scorer.calculateZ(abstractWeights, queries.getTerms());
							contentScores = scorer.calculateZ(contentWeights, queries.getTerms());

							for (Map.Entry<Long, Map<Long, Double>> entry : titleScores.entrySet()) {
								Map<Long, Double> newScores = new HashMap<>();
								for (Map.Entry<Long, Double> subentry : entry.getValue().entrySet()) {
									newScores.put(subentry.getKey(), subentry.getValue() * alphas.get("title"));
									titleScores.put(entry.getKey(), newScores);
								}
							}

							for (Map.Entry<Long, Map<Long, Double>> entry : categoryScores.entrySet()) {
								Map<Long, Double> newScores = new HashMap<>();
								for (Map.Entry<Long, Double> subentry : entry.getValue().entrySet()) {
									newScores.put(subentry.getKey(), subentry.getValue() * alphas.get("category"));
									categoryScores.put(entry.getKey(), newScores);
								}
							}

							for (Map.Entry<Long, Map<Long, Double>> entry : abstractScores.entrySet()) {
								Map<Long, Double> newScores = new HashMap<>();
								for (Map.Entry<Long, Double> subentry : entry.getValue().entrySet()) {
									newScores.put(subentry.getKey(), subentry.getValue() * alphas.get("abstract"));
									abstractScores.put(entry.getKey(), newScores);
								}
							}

							for (Map.Entry<Long, Map<Long, Double>> entry : contentScores.entrySet()) {
								Map<Long, Double> newScores = new HashMap<>();
								for (Map.Entry<Long, Double> subentry : entry.getValue().entrySet()) {
									newScores.put(subentry.getKey(), subentry.getValue() * alphas.get("content"));
									contentScores.put(entry.getKey(), newScores);
								}
							}

							scores = new TreeMap<>();

							Set<Long> docIds = xmlParser.getDocIds();
							for (Map.Entry<Long, String[]> entry : queries.getTerms().entrySet()) {
								Long queryId = entry.getKey();

								double titleS = 0.0;
								double categoryS = 0.0;
								double abstractS = 0.0;
								double contentS = 0.0;

								Map<Long, Double> sums = new HashMap<>();

								Iterator<Long> it = docIds.iterator();
								while (it.hasNext()) {
									Long docId = it.next();

									try {
										titleS = titleScores.get(queryId).get(docId);
									} catch (NullPointerException e) {
										titleS = 0.0;
									}

									try {
										categoryS = categoryScores.get(queryId).get(docId);
									} catch (NullPointerException e) {
										categoryS = 0.0;
									}

									try {
										abstractS = abstractScores.get(queryId).get(docId);
									} catch (NullPointerException e) {
										abstractS = 0.0;
									}

									try {
										contentS = contentScores.get(queryId).get(docId);
									} catch (NullPointerException e) {
										contentS = 0.0;
									}

									sums.put(docId, titleS + abstractS + categoryS + contentS);
								}
								scores.put(queryId, sums);
							}

							SortedMap<Long, Map<Long, Double>> finals = new TreeMap<>();
							for (Map.Entry<Long, Map<Long, Double>> entry : scores.entrySet()) {
								Long queryId = entry.getKey();
								Map<Long, Double> oldScores = entry.getValue();
								oldScores = Scorer.sortByValue(oldScores);
								Map<Long, Double> newScores = new HashMap<>();
								// take the 1st 1500
								int cpt = 1;
								for (Map.Entry<Long, Double> nentry : oldScores.entrySet()) {
									newScores.put(nentry.getKey(), nentry.getValue());
									if (cpt == 1500)
										break;
									else
										cpt++;
								}
								newScores = Scorer.sortByValue(newScores);
								finals.put(queryId, newScores);
							}

							scores = finals;
							break;
						default:
							return ERROR_MESSAGE;
						}
						outputer.output(scores, params[0], params[1], params[4], params[5], params[6], params[7],
								params[8], params[9], params[10]);
					} else {
						return ERROR_MESSAGE;
					}
				} else if (params[3].equals("sec")) {
					if (params[2].equals("text")) {
						return ERROR_MESSAGE;
					} else if (params[2].equals("xml")) {
						switch (params[4]) {
						case "ltn":
							secWeights = weighter.weightLTN(secInvertedIndex);
							break;
						case "ltc":
							secWeights = weighter.weightLTC(secInvertedIndex);
							break;
						case "bm25":
							secWeights = weighter.weightBM25(secInvertedIndex, xmlParser.getTermsNbrU(),
									Double.valueOf(params[5]), Double.valueOf(params[6]));
							break;
						case "bm25fa":
							return ERROR_MESSAGE;
						case "bm25fp":
							return ERROR_MESSAGE;
						default:
							return ERROR_MESSAGE;
						}
						secScores = scorer.calculate(secWeights, queries.getTerms());
						if (params[11].equals("0"))
							outputer.outputK(secScores, params[0], params[1], params[4], params[3], params[5],
									params[6]);
						else if (params[11].equals("1"))
							outputer.outputU(secScores, params[0], params[1], params[4], params[3], params[5],
									params[6]);
						else
							return ERROR_MESSAGE;
					} else {
						return ERROR_MESSAGE;
					}
				} else if (params[3].equals("p")) {
					if (params[2].equals("text")) {
						return ERROR_MESSAGE;
					} else if (params[2].equals("xml")) {
						switch (params[4]) {
						case "ltn":
							pWeights = weighter.weightLTN(pInvertedIndex);
							break;
						case "ltc":
							pWeights = weighter.weightLTC(pInvertedIndex);
							break;
						case "bm25":
							pWeights = weighter.weightBM25(pInvertedIndex, xmlParser.getTermsNbrX(),
									Double.valueOf(params[5]), Double.valueOf(params[6]));
							break;
						case "bm25fa":
							return ERROR_MESSAGE;
						case "bm25fp":
							return ERROR_MESSAGE;
						default:
							return ERROR_MESSAGE;
						}
						pScores = scorer.calculate(pWeights, queries.getTerms());
						if (params[11].equals("0"))
							outputer.outputK(pScores, params[0], params[1], params[4], params[3], params[5], params[6]);
						else if (params[11].equals("1"))
							outputer.outputU(pScores, params[0], params[1], params[4], params[3], params[5], params[6]);
						else
							return ERROR_MESSAGE;
					} else {
						return ERROR_MESSAGE;
					}
				} else
					return ERROR_MESSAGE;
			}
		}
		return "";
	}

}
