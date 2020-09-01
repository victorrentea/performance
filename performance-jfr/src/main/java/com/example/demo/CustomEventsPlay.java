package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

public class CustomEventsPlay {
   private static final Logger log = LoggerFactory.getLogger(CustomEventsPlay.class);

   public static void main(String[] args) throws IOException, InterruptedException {
      getStock("GOOGL");
      getStock("AMZN");
      getStock("AMZN");
      getStock("GOOGLe");
   }

   private static void getStock(String symbol) throws IOException, InterruptedException {
      CheckStockEvent checkStockEvent = new CheckStockEvent();
      checkStockEvent.begin();

      log.debug("Calling service for {} ", symbol);
      HttpClient client = HttpClient.newHttpClient();
      String uri = symbol.length() <= 5 ? "http://google.com" : "http://aaskdakkas.com"; // simulate timeout
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(uri))
          .build();

      HttpResponse<String> response = null;
      try {
         response = client.send(request, BodyHandlers.ofString());
         System.out.println(response.body());
      } finally {
         if (checkStockEvent.isEnabled()) {
            checkStockEvent.setSymbol(symbol);
            if (response != null) {
               checkStockEvent.setStatusCode(response.statusCode());
            }
            checkStockEvent.commit();
         }

      }


   }


}
