package ujm.dsc.ri.parse;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ujm.dsc.ri.clean.Cleaner;
import ujm.dsc.ri.core.Doc;
import ujm.dsc.ri.core.DocumentsCollection;

@Component
public class XmlParser {

	private static final Logger log = LogManager.getLogger(XmlParser.class.getName());

	public static final String XML_FOLDER_PATH = "input/coll/";

	@Autowired
	private Cleaner cleaner = new Cleaner();

	public static String printTags(Node nodes) {
		String content = "";
		if (nodes.hasChildNodes() || nodes.getNodeType() != 3) {
			content += nodes.getTextContent();
			// System.out.println("*********");
			NodeList nl = nodes.getChildNodes();
			for (int j = 0; j < nl.getLength(); j++)
				printTags(nl.item(j));
		}
		return content;
	}

	public DocumentsCollection parseTextFile(String filePath)
			throws IOException, ParserConfigurationException, SAXException {
		log.info("Indexing xml folder " + filePath);
		long start = System.currentTimeMillis();
		DocumentsCollection collection = new DocumentsCollection();
		List<File> files = (List<File>) FileUtils.listFiles(new File(XML_FOLDER_PATH), TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);
		for (File file : files) {
			Doc document = new Doc();
			Long id = Long.valueOf(file.getName().split(".xml")[0]);
			System.out.println(id);
			String content = "";
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			NodeList nl = doc.getDocumentElement().getChildNodes();
			for (int k = 0; k < nl.getLength(); k++) {
				content += printTags((Node) nl.item(k));
			}
			String[] splits = StringUtils.split(content);
			document.setId(id);
			for (String str : splits)
				document.addTerm(this.cleaner.cleanWord(str));
			collection.addDocument(document);

		}
		long end = System.currentTimeMillis();
		log.info("Done in " + (end - start) / 1000.00 + " seconds");
		return collection;
	}

}
