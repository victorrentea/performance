package victor.training.performance.leak;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static victor.training.performance.util.PerformanceUtil.sleepSeconds;

@RestController
@Slf4j
public class Leak4_XmlDom {
  public static final int LOTS_OF_XML_FILES = 1000;
  private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
  private static final XPathFactory xPathFactory = XPathFactory.newInstance();

  static void main() { // runnable standalone too
    new Leak4_XmlDom().countPlugins();
  }

  @GetMapping("leak4")
  public void countPlugins() {
    File xmlFile = new File("memory/pom.xml");
    log.info("Opening xml: " + xmlFile.getAbsolutePath());

    List<Node> modelVersion = new ArrayList<>();
    for (int i = 0; i < LOTS_OF_XML_FILES; i++) {
      modelVersion.addAll(extractElements(xmlFile, "//modelVersion"));
    }
    // <modelVersion>4.0.0</modelVersion>

    log.info("Found {} nodes", modelVersion.size());
    Node first = modelVersion.get(0);
    log.info("First: class={}, text: {}", first.getClass().getSimpleName(), first.getTextContent());

    sleepSeconds(30); // time to take a heap dump
  }

  // TODO how many instances of DeferredElementImpl are in heapdump? Justify?
  // eg RUN this in OQL in jvisualVM: select x.name from com.sun.org.apache.xerces.internal.dom.DeferredElementImpl x

  // === === === === === === === Support code  === === === === === === ===

  @SneakyThrows
  public static List<Node> extractElements(File xmlFile, String nodePath) {
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    if (!xmlFile.isFile()) {
      throw new IllegalArgumentException("Not a pom file: " + xmlFile.getAbsolutePath());
    }

    try (FileInputStream xmlStream = new FileInputStream(xmlFile)) {
      Document doc = documentBuilder.parse(xmlStream);
      XPath xPath = xPathFactory.newXPath();
      XPathExpression expression = xPath.compile(nodePath);

      List<Node> pluginNodes = new ArrayList<>();
      NodeList nodeList = (NodeList) expression.evaluate(doc.getDocumentElement(), XPathConstants.NODESET);
      for (int i = 0; i < nodeList.getLength(); i++) {
        pluginNodes.add(nodeList.item(i));
      }
      return pluginNodes;
    }
  }
}
