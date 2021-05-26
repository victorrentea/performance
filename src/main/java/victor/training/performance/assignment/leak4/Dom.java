package victor.training.performance.assignment.leak4;


import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import victor.training.performance.PerformanceUtil;

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
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Dom {

   private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
   private static final XPathFactory xPathFactory = XPathFactory.newInstance();

   public static void main(String[] args) {

      List<File> pomFiles = Stream.generate(() -> new File("pom.xml")).limit(1_000).collect(toList());
      List<Node> nodes = extractArtifactIdFromPomFiles(pomFiles);

      System.out.println("Loaded nodes: " + nodes.size());
      // How many instances of DeferredElementImpl (= a parsed XML node) do you expect to have in memory here?

      System.out.println("Used heap: " + PerformanceUtil.getUsedHeap());
      if (PerformanceUtil.getUsedHeapBytes() > 20_000_000) {
         System.err.println("GOAL NOT MET. LEAK STILL PRESENT");
      }
      System.out.println("Take a heap dump");
      PerformanceUtil.waitForEnter();

      List<String> artifactIds = nodes.stream().map(Node::getTextContent).collect(toList());
   }

   @SneakyThrows
   public static List<Node> extractArtifactIdFromPomFiles(List<File> pomFiles) {
      List<Node> result = new ArrayList<>();
      for (File pomFile : pomFiles) {
         DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
         try (FileInputStream xmlStream = new FileInputStream(pomFile)) {
            Document doc = documentBuilder.parse(xmlStream);
            XPath xPath = xPathFactory.newXPath();
            XPathExpression expression = xPath.compile("/project/artifactId");

            Node node = (Node) expression.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
            result.add(node);
         }
      }
      return result;
   }
}
