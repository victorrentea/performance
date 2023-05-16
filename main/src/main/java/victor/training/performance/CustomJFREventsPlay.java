package victor.training.performance;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.util.PerformanceUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

// TODO check this out : http://hirt.se/blog/?tag=jfr
public class CustomJFREventsPlay {
   private static final Logger log = LoggerFactory.getLogger(CustomJFREventsPlay.class);

   public static void main(String[] args) throws IOException, InterruptedException {
      PerformanceUtil.printJfrFile();
      getStock("GOOGL");
      getStock("AMZN");
      getStock("AMZN");
      getStock("GOOGLe");
   }

   private static void getStock(String symbol) throws IOException, InterruptedException {

      CheckStockJFREvent jfrEvent = new CheckStockJFREvent();
      jfrEvent.begin();

      log.debug("Calling service for {} ", symbol);
      HttpClient client = HttpClient.newHttpClient();
      String uri = symbol.length() <= 5 ? "http://google.com" : "http://aaskdakkas.com"; // simulate timeout
      HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).build();


      PerformanceUtil.sleepMillis(100000);

      HttpResponse<String> response = null;
      try {
         response = client.send(request, BodyHandlers.ofString());
         System.out.println(response.body());
      } finally {
         if (jfrEvent.isEnabled()) {
            // do some expensive CPU work to prepare more data to set on it, eg JSON serialization to a String
         }
         if (jfrEvent.isEnabled()) {
            jfrEvent.setSymbol(symbol);
            if (response != null) {
               jfrEvent.setStatusCode(response.statusCode());
            }
            jfrEvent.commit();
         }
      }
   }

   @Category("Business")
   //@Threshold("100 ms")
   public static class CheckStockJFREvent extends Event {

      @Label("Stock Symbol")
      private String symbol;
      @Description("Return HTTP Status code")
      private int statusCode;
      // only primitives allowed

      public void setSymbol(String symbol) {
         this.symbol = symbol;
      }

      public void setStatusCode(int statusCode) {
         this.statusCode = statusCode;
      }
   }

}