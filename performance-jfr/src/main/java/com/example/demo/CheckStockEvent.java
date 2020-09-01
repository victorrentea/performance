package com.example.demo;

import jdk.jfr.Category;
import jdk.jfr.Event;
import jdk.jfr.Threshold;


@Category("Business")
//@Threshold("100 ms")
public class CheckStockEvent extends Event {

   private String symbol;
   private int statusCode;

   public void setSymbol(String symbol) {
      this.symbol = symbol;
   }

   public void setStatusCode(int statusCode) {
      this.statusCode = statusCode;
   }

}
