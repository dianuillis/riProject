package ujm.dsc.ri.parse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lombok.Data;
import ujm.dsc.ri.clean.Cleaner;
import ujm.dsc.ri.core.InvertedIndex;

@Component
@Data
public class XmlParser {

	public static final String XML_FOLDER_PATH = "input/coll/"; // TODO fix this
																// shit col
	public static final String TEST_XML_FOLDER_PATH = "input/coll2/";
	private static final Logger log = LogManager.getLogger(XmlParser.class.getName());

	@Autowired
	private Cleaner cleaner;

	private SortedMap<Long, Double> termsNbr; // for article

	private SortedMap<String, Double> termsNbrU; // for sec

	private SortedMap<String, Double> termsNbrX; // for p

	private Map<String, SortedMap<Long, Double>> termsNbrF; // for bm25f

	private Set<Long> docIds;

	private String extractNonSec(Node node) {
		String content = "";
		if ((node.hasChildNodes() || node.getNodeType() != 3)) {
			if (!node.getNodeName().equalsIgnoreCase("sec")) {
				content += node.getTextContent();
				NodeList nl = node.getChildNodes();
				for (int j = 0; j < nl.getLength(); j++)
					extractNonSec(nl.item(j));
			} else
				return "";
		}
		return content;
	}

	private String extractText(Node nodes) {
		String content = "";
		if (nodes.hasChildNodes() || nodes.getNodeType() != 3) {
			content += nodes.getTextContent();
			NodeList nl = nodes.getChildNodes();
			for (int j = 0; j < nl.getLength(); j++)
				extractText(nl.item(j));
		}
		return content;
	}

	private int getOrder(Node node) {
		String name = node.getNodeName();
		int cpt = 1;
		while (node.getPreviousSibling() != null) {
			node = node.getPreviousSibling();
			if (node.getNodeName().equals(name)) {
				cpt++;
			}
		}
		return cpt;
	}

	private String getXpath(Node node) {
		String path = "";
		while (node != null) {
			path = node.getNodeName() + "[" + this.getOrder(node) + "]" + "/" + path;
			node = node.getParentNode();
		}
		return path;
	}

	public InvertedIndex<Long> parse(String filePath, Set<String> uniqueTerms)
			throws IOException, ParserConfigurationException, SAXException {
		log.info("Indexing xml folder " + filePath);
		this.termsNbr = new TreeMap<>();
		long start = System.currentTimeMillis();
		InvertedIndex<Long> index = new InvertedIndex<Long>();
		List<File> files = (List<File>) FileUtils.listFiles(new File(filePath), TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);
		for (File file : files) {
			Long docId = Long.valueOf(file.getName().split(".xml")[0]);
			index.setDocNbr(index.getDocNbr() + 1);
			String content = "";
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			org.w3c.dom.Document doc = dBuilder.parse(file);
			NodeList nl = doc.getDocumentElement().getChildNodes();
			for (int k = 0; k < nl.getLength(); k++) {
				content += extractText(nl.item(k));
			}
			String[] words = cleaner.cleanLine(StringUtils.split(content));
			for (String term : words) {
				if (uniqueTerms.contains(term))
					index.addTerm(term, docId, 1.0);
			}
			termsNbr.put(docId, (double) words.length);
		}
		long end = System.currentTimeMillis();
		log.info("Done in " + (end - start) / 1000.00 + " seconds");
		return index;
	}

	public InvertedIndex<Long> parseF(String filePath, Set<String> uniqueTerms, Map<String, Double> alphas)
			throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		log.info("Indexing xml folder " + filePath);
		this.termsNbr = new TreeMap<>();
		long start = System.currentTimeMillis();
		InvertedIndex<Long> index = new InvertedIndex<Long>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		List<File> files = (List<File>) FileUtils.listFiles(new File(filePath), TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);
		for (File file : files) {
			Long docId = Long.valueOf(file.getName().split(".xml")[0]);
			index.setDocNbr(index.getDocNbr() + 1);

			Document xml = builder.parse(file);
			Element root = xml.getDocumentElement();
			XPathFactory xpf = XPathFactory.newInstance();
			XPath path = xpf.newXPath();

			String title = root.getElementsByTagName("title").item(0).getTextContent();
			title = title.trim().replaceAll(" +", " ");
			String categories = "";
			String abstr = "";
			String content = "";

			// getting sections
			boolean sectionsLeft = true;
			int sCpt = 1;
			String section = "";
			loop: while (sectionsLeft) {
				String expression = "//sec[" + sCpt + "]";
				section = (String) path.evaluate(expression, root);
				if (section.isEmpty() || section.trim().equals(" "))
					break loop;
				else
					content += section;
				sCpt++;
			}
			content = content.trim().replaceAll(" +", " ");

			// getting abstract
			NodeList nl = root.getElementsByTagName("bdy");
			for (int k = 0; k < nl.getLength(); k++) {
				Node bdy = nl.item(k);
				this.removeSec(bdy);
				abstr += extractNonSec(bdy);
			}
			abstr = abstr.trim().replaceAll(" +", " ");

			// getting title
			nl = root.getElementsByTagName("categories");
			for (int k = 0; k < nl.getLength(); k++) {
				categories += extractText(nl.item(k));
			}
			categories = categories.trim().replaceAll(" +", " ");

			double cpt = 0.0;

			String[] words = cleaner.cleanLine(StringUtils.split(title));
			for (String term : words) {
				if (uniqueTerms.contains(term)) {
					index.addTerm(term, docId, alphas.get("title"));
					cpt += alphas.get("title");
				} else {
					cpt += 1.0;
				}
			}

			words = cleaner.cleanLine(StringUtils.split(categories));
			for (String term : words) {
				if (uniqueTerms.contains(term)) {
					index.addTerm(term, docId, alphas.get("category"));
					cpt += alphas.get("category");
				} else {
					cpt += 1.0;
				}
			}

			words = cleaner.cleanLine(StringUtils.split(abstr));
			for (String term : words) {
				if (uniqueTerms.contains(term)) {
					index.addTerm(term, docId, alphas.get("abstract"));
					cpt += alphas.get("abstract");
				} else {
					cpt += 1.0;
				}
			}

			words = cleaner.cleanLine(StringUtils.split(content));
			for (String term : words) {
				if (uniqueTerms.contains(term)) {
					index.addTerm(term, docId, alphas.get("content"));
					cpt += alphas.get("content");
				} else {
					cpt += 1.0;
				}
			}

			termsNbr.put(docId, cpt);
		}
		long end = System.currentTimeMillis();
		log.info("Done in " + (end - start) / 1000.00 + " seconds");
		return index;
	}

	public InvertedIndex<String> parseK(String filePath, Set<String> uniqueTerms)
			throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		log.info("Indexing xml folder " + filePath);
		this.termsNbrX = new TreeMap<>();
		long start = System.currentTimeMillis();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InvertedIndex<String> index = new InvertedIndex<String>();
		List<File> files = (List<File>) FileUtils.listFiles(new File(filePath), TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);
		for (File file : files) {
			String docId = file.getName().split(".xml")[0];
			Document xml = builder.parse(file);
			Element root = xml.getDocumentElement();
			XPathFactory xpf = XPathFactory.newInstance();
			XPath path = xpf.newXPath();

			boolean sectionsLeft = true;
			int sCpt = 1;
			// p
			loop: while (sectionsLeft) {
				String expression = "//bdy[1]/p[" + sCpt + "]";
				String content = (String) path.evaluate(expression, root);
				Node node = (Node) path.evaluate(expression, root, XPathConstants.NODE);
				content = content.trim().replaceAll(" +", " ");
				if (content.isEmpty() || content.equals(" "))
					break loop;
				else {
					String xpath = this.getXpath(node);
					StringBuilder xpathb = new StringBuilder(xpath);
					xpathb.setCharAt(21, '1');
					xpath = xpathb.toString();
					xpath = xpath.substring(11, xpath.length() - 1);
					String[] words = cleaner.cleanLine(StringUtils.split(content));
					for (String term : words) {
						if (uniqueTerms.contains(term))
							index.addTerm(term, docId + "?" + xpath, 1.0);
					}
					termsNbrX.put(docId + "?" + xpath, (double) words.length);
					sCpt++;
					index.setDocNbr(index.getDocNbr() + 1);
				}
			}

			// sec/p
			sectionsLeft = true;
			boolean paragraphsLeft = true;
			sCpt = 1;
			int pCpt = 1;
			loops: while (sectionsLeft) {
				String expression = "//sec[" + sCpt + "]";
				String content = (String) path.evaluate(expression, root);
				content = content.trim().replaceAll(" +", " ");
				if (content.isEmpty() || content.equals(" "))
					break loops;
				else {
					loopp: while (paragraphsLeft) {
						expression = "//sec[" + sCpt + "]//p[" + pCpt + "]";
						content = (String) path.evaluate(expression, root);
						Node node = (Node) path.evaluate(expression, root, XPathConstants.NODE);
						content = content.trim().replaceAll(" +", " ");
						if (content.isEmpty() || content.equals(" ")) {
							break loopp;
						} else {
							String xpath = this.getXpath(node);
							StringBuilder xpathb = new StringBuilder(xpath);
							xpathb.setCharAt(21, '1');
							xpath = xpathb.toString();
							xpath = xpath.substring(11, xpath.length() - 1);
							String[] words = cleaner.cleanLine(StringUtils.split(content));
							for (String term : words) {
								if (uniqueTerms.contains(term))
									index.addTerm(term, docId + "?" + xpath, 1.0);
							}
							termsNbrX.put(docId + "?" + xpath, (double) words.length);
							pCpt++;
							index.setDocNbr(index.getDocNbr() + 1);
						}
					}
					sCpt++;
					pCpt = 1;
				}
			}
		}

		long end = System.currentTimeMillis();
		log.info("Done in " + (end - start) / 1000.00 + " seconds");
		return index;
	}

	public InvertedIndex<String> parseU(String filePath, Set<String> uniqueTerms)
			throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		log.info("Indexing xml folder " + filePath);
		this.termsNbrU = new TreeMap<>();
		long start = System.currentTimeMillis();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InvertedIndex<String> index = new InvertedIndex<String>();
		List<File> files = (List<File>) FileUtils.listFiles(new File(filePath), TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);
		for (File file : files) {
			String docId = file.getName().split(".xml")[0];
			Document xml = builder.parse(file);
			Element root = xml.getDocumentElement();
			XPathFactory xpf = XPathFactory.newInstance();
			XPath path = xpf.newXPath();
			boolean sectionsLeft = true;
			int sCpt = 1;
			loop: while (sectionsLeft) {
				String expression = "//sec[" + sCpt + "]";
				String content = (String) path.evaluate(expression, root);
				Node node = (Node) path.evaluate(expression, root, XPathConstants.NODE);
				content = content.trim().replaceAll(" +", " ");
				if (content.isEmpty() || content.equals(" "))
					break loop;
				else {
					String xpath = this.getXpath(node);
					StringBuilder xpathb = new StringBuilder(xpath);
					xpathb.setCharAt(21, '1');
					xpath = xpathb.toString();
					xpath = xpath.substring(11, xpath.length() - 1);
					String[] words = cleaner.cleanLine(StringUtils.split(content));
					for (String term : words) {
						if (uniqueTerms.contains(term))
							index.addTerm(term, docId + "?" + xpath, 1.0);
					}
					termsNbrU.put(docId + "?" + xpath, (double) words.length);
					sCpt++;
					index.setDocNbr(index.getDocNbr() + 1);
				}
			}
		}
		long end = System.currentTimeMillis();
		log.info("Done in " + (end - start) / 1000.00 + " seconds");
		return index;
	}

	public Map<String, InvertedIndex<Long>> parseZ(String filePath, Set<String> uniqueTerms, Map<String, Double> alphas)
			throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
		log.info("Indexing xml folder " + filePath);
		long start = System.currentTimeMillis();

		this.docIds = new HashSet<>();
		this.termsNbrF = new HashMap<>();
		SortedMap<Long, Double> titleTermNbr = new TreeMap<>();
		SortedMap<Long, Double> categoryTermNbr = new TreeMap<>();
		SortedMap<Long, Double> abstractTermNbr = new TreeMap<>();
		SortedMap<Long, Double> contentTermNbr = new TreeMap<>();

		Map<String, InvertedIndex<Long>> indexes = new HashMap<>();
		InvertedIndex<Long> titleIndex = new InvertedIndex<Long>();
		InvertedIndex<Long> categoryIndex = new InvertedIndex<Long>();
		InvertedIndex<Long> abstractIndex = new InvertedIndex<Long>();
		InvertedIndex<Long> contentIndex = new InvertedIndex<Long>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		List<File> files = (List<File>) FileUtils.listFiles(new File(filePath), TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);
		for (File file : files) {
			Long docId = Long.valueOf(file.getName().split(".xml")[0]);
			this.docIds.add(docId);

			Document xml = builder.parse(file);
			Element root = xml.getDocumentElement();
			XPathFactory xpf = XPathFactory.newInstance();
			XPath path = xpf.newXPath();

			// TODO check if title exists
			// indexing title
			Node nodeTitle = root.getElementsByTagName("title").item(0);
			if (nodeTitle != null) {
				String title = nodeTitle.getTextContent();
				titleIndex.setDocNbr(titleIndex.getDocNbr() + 1);
				title = title.trim().replaceAll(" +", " ");
				double cptTitle = 0.0;
				String[] words = cleaner.cleanLine(StringUtils.split(title));
				for (String term : words) {
					if (uniqueTerms.contains(term)) {
						titleIndex.addTerm(term, docId, 1.0);
						cptTitle += alphas.get("title"); // TODO generate run
															// with
															// +1
						// cptTitle += 1.0;
					} else {
						cptTitle += 1.0;
					}
				}
				titleTermNbr.put(docId, cptTitle);
			} else {
				// TODO check this
			}

			String categories = "";
			String abstr = "";
			String content = "";

			// indexing content (sections)
			boolean sectionsLeft = true;
			int sCpt = 1;
			String section = "";
			loop: while (sectionsLeft) {
				String expression = "//sec[" + sCpt + "]";
				section = (String) path.evaluate(expression, root);
				if (section.isEmpty() || section.trim().equals(" "))
					break loop;
				else
					content += section;
				sCpt++;
			}
			if (!content.isEmpty()) {
				contentIndex.setDocNbr(contentIndex.getDocNbr() + 1.0);
				content = content.trim().replaceAll(" +", " ");
				String[] words = cleaner.cleanLine(StringUtils.split(content));
				double cptContent = 0.0;
				for (String term : words) {
					if (uniqueTerms.contains(term)) {
						contentIndex.addTerm(term, docId, 1);
						cptContent += alphas.get("content"); // TODO generate
																// run
																// with +1
						// cptContent += 1.0;
					} else {
						cptContent += 1.0;
					}
				}
				contentTermNbr.put(docId, cptContent);
			} else {

			}

			// indexing abstract
			NodeList nl = root.getElementsByTagName("bdy");
			for (int k = 0; k < nl.getLength(); k++) {
				Node bdy = nl.item(k);
				this.removeSec(bdy);
				abstr += extractNonSec(bdy);
			}
			if (!abstr.isEmpty()) {
				abstractIndex.setDocNbr(abstractIndex.getDocNbr() + 1.0);
				abstr = abstr.trim().replaceAll(" +", " ");
				String[] words = cleaner.cleanLine(StringUtils.split(abstr));
				double cptAbstract = 0.0;
				for (String term : words) {
					if (uniqueTerms.contains(term)) {
						abstractIndex.addTerm(term, docId, 1);
						cptAbstract += alphas.get("abstract"); // TODO generate
																// run
																// with +1
						// cptAbstract += 1.0;
					} else {
						cptAbstract += 1.0;
					}
				}
				abstractTermNbr.put(docId, cptAbstract);
			} else {
				// TODO
			}

			// indexing categories
			nl = root.getElementsByTagName("categories");
			for (int k = 0; k < nl.getLength(); k++) {
				categories += extractText(nl.item(k));
			}
			if (!categories.isEmpty()) {
				categoryIndex.setDocNbr(categoryIndex.getDocNbr() + 1.0);
				categories = categories.trim().replaceAll(" +", " ");
				String[] words = cleaner.cleanLine(StringUtils.split(categories));
				double cptCategory = 0.0;
				for (String term : words) {
					if (uniqueTerms.contains(term)) {
						categoryIndex.addTerm(term, docId, 1);
						cptCategory += alphas.get("category"); // TODO generate
																// run
																// with +1
						// cptCategory += 1.0;
					} else {
						cptCategory += 1.0;
					}
				}
				categoryTermNbr.put(docId, cptCategory);
			} else {
				// TODO
			}

			indexes.put("title", titleIndex);
			indexes.put("category", categoryIndex);
			indexes.put("abstract", abstractIndex);
			indexes.put("content", contentIndex);

			this.termsNbrF.put("title", titleTermNbr);
			this.termsNbrF.put("category", categoryTermNbr);
			this.termsNbrF.put("abstract", abstractTermNbr);
			this.termsNbrF.put("content", contentTermNbr);
		}
		long end = System.currentTimeMillis();
		log.info("Done in " + (end - start) / 1000.00 + " seconds");
		return indexes;
	}

	private Node removeSec(Node node) {
		for (int k = 0; k < node.getChildNodes().getLength(); k++) {
			if (node.getChildNodes().item(k).getNodeName().equals("sec")) {
				node.removeChild(node.getChildNodes().item(k));
			}
		}
		return node;
	}

}
