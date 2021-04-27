package victor.training.performance.leaks;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("leak10")
@Slf4j
public class Leak10_XmlDom {

   public static final int LOTS_OF_XMLs = 1000;
   private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
   private static final XPathFactory xPathFactory = XPathFactory.newInstance();

   @GetMapping
   public void countPlugins(HttpServletResponse response) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {

      List<Node> allDependencies = new ArrayList<>();

      for (int x = 0; x < LOTS_OF_XMLs; x++) {
         List<Node> pluginNodes = extractPlugins();
         allDependencies.addAll(pluginNodes);
      }

      log.info("Loaded {} nodes", allDependencies.size());
      log.info(allDependencies.get(0).getClass().toString());


      // How many Node instances do you expect to have in memory here ?
      while (true) { // imagine a long task here...
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
