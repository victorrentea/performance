package victor.training.performance.leaks;


import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Slf4j
public class LeakDom {

   public static final int LOTS_OF_XMLs = 1000;
   private static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
   private static XPathFactory xPathFactory = XPathFactory.newInstance();

   public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {

      List<Node> allDependencies = new ArrayList<>();

      for (int x = 0; x < LOTS_OF_XMLs; x++) {
         List<Node> pluginNodes = extractPlugins();
         allDependencies.addAll(pluginNodes);
      }

      log.info("Loaded {} nodes", allDependencies.size());
      log.info(allDependencies.get(0).getClass().toString());
      // How many Node instances do you expect to have in memory here ?
      while (true) {
      }

      // RUN this in OQL in jvisualVM : select x.name from com.sun.org.apache.xerces.internal.dom.DeferredElementImpl x
   }

   private static List<Node> extractPlugins() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      try (FileInputStream xmlStream = new FileInputStream("pom.xml")) {
         Document doc = documentBuilder.parse(xmlStream);
         XPath xPath = xPathFactory.newXPath();
         XPathExpression expression = xPath.compile("//plugin");

         List<Node> pluginNodes = new ArrayList<>();
         NodeList nodeList = (NodeList) expression.evaluate(doc.getDocumentElement(), XPathConstants.NODESET);
         for (int i = 0; i < nodeList.getLength(); i++) {
            pluginNodes.add(nodeList.item(i));
         }
         return pluginNodes;
      }
   }
}
