package victor.training.performance.batch.core;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
public class CountingTotalItemsStepListener implements StepExecutionListener {
   @Value("#{jobExecutionContext['START_TIME']}")
   private String startTime;

   @Value("#{jobParameters['FILE_PATH']}")
   private String filePath;

   @SneakyThrows
   @Override
   public void beforeStep(StepExecution stepExecution) {
      log.debug("step exec context: " + stepExecution.getExecutionContext());
      log.debug("Job exec context: " + stepExecution.getJobExecution().getExecutionContext());

      int count = countTagsInFile(new File(filePath), "/personList/person");
      log.info("Counted {} items in the file", count);
      log.info(" -- Starting data MAIN import -- ");
      stepExecution.getExecutionContext().put("TOTAL_ITEM_COUNT", count);
   }

   private int countTagsInFile(File file, String xpathExpression) throws IOException, XPathExpressionException {
      log.info("Counting items in file {}", file);
      try (FileInputStream inputStream = new FileInputStream(file)) {
         XPathFactory xPathFactory = XPathFactory.newInstance();
         XPath xPath = xPathFactory.newXPath();
         XPathExpression expr = xPath.compile(xpathExpression);
         InputSource source = new InputSource(inputStream);
         NodeList list = (NodeList) expr.evaluate(source, XPathConstants.NODESET);
        return list.getLength();
      }
   }

   @Override
   public ExitStatus afterStep(StepExecution stepExecution) {
      return null;
   }
}
