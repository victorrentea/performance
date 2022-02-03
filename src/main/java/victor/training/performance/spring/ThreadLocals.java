package victor.training.performance.spring;

import victor.training.performance.util.PerformanceUtil;

public class ThreadLocals {

   public static void main(String[] args) {
      LaColindat emma = new LaColindat();
      LaColindat narcis = new LaColindat();

      new Thread(() -> {
         LaColindat.colindCuInterres();
         LaColindat.colindCuInterres();
         LaColindat.colindCuInterres();
         LaColindat.colindCuInterres();
         LaColindat.colindCuInterres();
         m1();
      }).start();

      new Thread(() -> {
         LaColindat.colindCuInterres();
         LaColindat.colindCuInterres();
         LaColindat.colindCuInterres();
         LaColindat.colindCuInterres();
         LaColindat.colindCuInterres();


         m1();
      }).start();

   }

   private static void m1() {
      m3();
   }

   private static void m3() {
      m2();
   }

   private static void m2() {
      System.out.println(LaColindat.catAmStrans());

      // 100% din voi ati folosit THread Local [fara sa reazliati]
      // JDBC, PersistenceContext, @Transactional, @Scope("request" sau "session"), SecurityContextHolder, MDC.
      }
}

class LaColindat {
   private static ThreadLocal<Integer> covrigi = ThreadLocal.withInitial(() -> 0 );

   public static void colindCuInterres() {


//      Logback MDC poti pune chestii utle de pus in log de atunci mai departe pe tot ce faci in acel thread..
//      de exemplu current username pus de la inceputul fluxului: apare in log
      PerformanceUtil.sleepq(100);
      covrigi.set(covrigi.get() + 1);
   }

   public static int catAmStrans() {
      return covrigi.get();
   }
}