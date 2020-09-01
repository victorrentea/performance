module performance.jfr {
   requires jdk.jfr;
   requires spring.boot.autoconfigure;
   requires spring.boot;
   opens victor.training.jfr to spring.core;
}