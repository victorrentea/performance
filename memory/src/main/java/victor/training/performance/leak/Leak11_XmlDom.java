package victor.training.performance.leak;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import victor.training.performance.util.PerformanceUtil;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("leak11")
@Slf4j
public class Leak11_XmlDom {
   public static final int LOTS_OF_XMLs = 1000;
   private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
   private static final XPathFactory xPathFactory = XPathFactory.newInstance();

   @GetMapping
   public void countPlugins() {
      List<Node> allDependencies = new ArrayList<>();
      for (int x = 0; x < LOTS_OF_XMLs; x++) {
         List<Node> pluginNodes = extractPluginsFromPom();
         allDependencies.addAll(pluginNodes);
      }

      log.info("Loaded {} <dependency> nodes", allDependencies.size());
      log.info(allDependencies.get(0).getClass().toString());

      // How many Node instances do you expect to have in memory here ?
      PerformanceUtil.sleepMillis(100_000); // imagine a long task here...

      // RUN this in OQL in jvisualVM : select x.name from com.sun.org.apache.xerces.internal.dom.DeferredElementImpl x
   }

   private List<Node> extractPluginsFromPom(int n) {
      return IntStream.range(0, n)
          .mapToObj(this::extractPluginsFromPom)
          .flatMap(Collection::stream)
          .collect(Collectors.toList());
   }
   @SneakyThrows
   public static List<Node> extractPluginsFromPom() {
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      File pomFile = new File("pom.xml");
      if (!pomFile.isFile()) {
         throw new IllegalArgumentException("Not a pom file: " + pomFile.getAbsolutePath());
      }

      try (FileInputStream xmlStream = new FileInputStream(pomFile)) {
         Document doc = documentBuilder.parse(xmlStream);
         XPath xPath = xPathFactory.newXPath();
         XPathExpression expression = xPath.compile("//plugin");


         List<Node> pluginNodes = new ArrayList<>();
         NodeList nodeList = (NodeList) expression.evaluate(doc.getDocumentElement(), XPathConstants.NODESET);
         for (int i = 0; i<nodeList.getLength();i++)

         {
            pluginNodes.add(nodeList.item(i));
         }
         return pluginNodes;
      }
   }
}
