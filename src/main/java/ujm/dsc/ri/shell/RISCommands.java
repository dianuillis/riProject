package ujm.dsc.ri.shell;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import ujm.dsc.ri.core.Doc;
import ujm.dsc.ri.core.DocumentsCollection;
import ujm.dsc.ri.core.QueriesCollection;
import ujm.dsc.ri.io.IOlizer;
import ujm.dsc.ri.output.Outputer;
import ujm.dsc.ri.parse.QueryParser;
import ujm.dsc.ri.parse.TextParser;
import ujm.dsc.ri.parse.XmlParser;
import ujm.dsc.ri.score.Scorer;
import ujm.dsc.ri.weight.Weighter;

@Component
public class RISCommands implements CommandMarker {

	@Autowired
	private TextParser textParser;

	@Autowired
	private QueryParser queryParser;

	@Autowired
	private XmlParser xmlParser;

	@Autowired
	private IOlizer iolizer;

	@Autowired
	private Weighter weighter;

	@Autowired
	private Scorer scorer;

	@Autowired
	private Outputer outputer;

	private DocumentsCollection documentsCollection;
	private QueriesCollection queriesCollection;
	private SortedMap<String, SortedMap<Long, Double>> docWeights;
	private SortedMap<String, SortedMap<Long, Double>> queryWeights;
	private Map<Long, Double> scores;
	private SortedMap<Long, Map<Long, Double>> ranks;

	@CliAvailabilityIndicator({ "!", "//", ":", "date", "script", "system properties", "version" })
	public boolean isDefaultAvailable() {
		return false;
	}

	@CliAvailabilityIndicator({ "indexText", "indexXML", "indexQuery", "saveIndexToFile", "readIndexFromFile",
			"readQueryFromFile", "weight", "rank", "output", "about", "launch" })
	public boolean isCommandAvailable() {
		return true;
	}

	@CliCommand(value = "indexText", help = "index text file")
	public String indexText(@CliOption(key = {
			"file" }, mandatory = false, unspecifiedDefaultValue = TextParser.DOC_TEXT_PATH, help = "Path of the text file to index") final String file) {
		try {
			this.documentsCollection = this.textParser.parseTextFile(file);
			// this.iolizer.serialize(this.documentsCollection, "index.bin");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@CliCommand(value = "indexXML", help = "index all xml files in a directory")
	public String indexXML(@CliOption(key = {
			"directory" }, mandatory = false, unspecifiedDefaultValue = "", help = "Path of the xml directory to index") final String directory) {
		return "";
	}

	@CliCommand(value = "indexQuery", help = "index query file")
	public String indexQuery(@CliOption(key = {
			"file" }, mandatory = false, unspecifiedDefaultValue = QueryParser.QUERY_PATH, help = "Path of the query file to index") final String file) {
		try {
			this.queriesCollection = this.queryParser.parseQueryFile(file);
			this.iolizer.serialize(this.queriesCollection, "query.bin");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@CliCommand(value = "saveIndexToFile", help = "index datastructure to binary file")
	public String saveIndexToFile(@CliOption(key = {
			"file" }, mandatory = true, unspecifiedDefaultValue = "index.bin", help = "Path of the binary file where to save the index") final String file) {
		return "";
	}

	@CliCommand(value = "readIndexFromFile", help = "binary file to index datastructure")
	public String readIndexFromFile(@CliOption(key = {
			"file" }, mandatory = false, unspecifiedDefaultValue = "index.bin", help = "Path of the binary file from where to read the index") final String file) {
		try {
			this.documentsCollection = (DocumentsCollection) this.iolizer.dserialize(file);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@CliCommand(value = "readQueryFromFile", help = "binary file to query datastructure")
	public String readQueryFromFile(@CliOption(key = {
			"file" }, mandatory = false, unspecifiedDefaultValue = "query.bin", help = "Path of the binary file from where to read the query") final String file) {
		try {
			this.queriesCollection = (QueriesCollection) this.iolizer.dserialize(file);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@CliCommand(value = "weight", help = "process documents and queries weightings")
	public String weight(@CliOption(key = {
			"fct" }, mandatory = false, unspecifiedDefaultValue = "ltn", help = "weighting function to use (ltn, ltc)") final String fct) {
		this.docWeights = this.weighter.weightDocsLTN(documentsCollection, queriesCollection);
		this.queryWeights = this.weighter.weightQueryLTN(documentsCollection, queriesCollection);
		return "";
	}

	@CliCommand(value = "score", help = "process documents scorings")
	public String score() {
		this.ranks = this.scorer.scoreDocsLtn(docWeights, queryWeights);
		return "";
	}

	@CliCommand(value = "output", help = "output runs")
	public String output() {
		return "";
	}

	@CliCommand(value = "launch", help = "process runs")
	public String launch() throws ParserConfigurationException, SAXException {
		int cpt = 1;
		try {
			this.documentsCollection = this.xmlParser.parseTextFile("");
			this.queriesCollection = this.queryParser.parseQueryFile("");
			DocumentsCollection tmp = new DocumentsCollection();
			Iterator<Doc> diterator = documentsCollection.getDocuments().iterator();
			while (diterator.hasNext()) {
				tmp.addDocument(diterator.next());
				if (tmp.getDocuments().size() == 1500) {
					this.docWeights = this.weighter.weightDocsLTN(tmp, queriesCollection);
					this.queryWeights = this.weighter.weightQueryLTN(tmp, queriesCollection);
					this.ranks = this.scorer.scoreDocsLtn(docWeights, queryWeights);
					this.outputer.output(this.ranks, cpt);
					tmp.getDocuments().clear();
					cpt++;
				}
			}
			// this.iolizer.serialize(this.documentsCollection, "index.bin");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	@CliCommand(value = "about", help = "app creators")
	public String about() {
		return "Diana RAMIREZ, Alhassan BAH, Yassin BENDARI, Hamza BAAZIZ - M2 DSC - UJM 2016/17";
	}

}
