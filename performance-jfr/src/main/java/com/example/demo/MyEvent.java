package com.example.demo;

import jdk.jfr.Event;

public class MyEvent extends Event {
   private String message;

   public void setMessage(String message) {
      this.message = message;
   }
}
