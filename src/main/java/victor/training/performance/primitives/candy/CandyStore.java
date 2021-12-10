package victor.training.performance.primitives.candy;

import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class CandyStore {


   public static void main(String[] args) {
      ExecutorService pool = Executors.newCachedThreadPool();

      TableModel model = new TableModel(new CandyClassificationHandler(), pool);
      model.setDataModel(new TableDataModel());
      Consumer<Candy> sendMethod = getSendMethod(model);


      ExecutorService candyFactories = Executors.newCachedThreadPool();
      for (int i = 0; i < 20; i++) {
         candyFactories.submit(() -> {
            for (int j = 0; j < 1000; j++) {
               PerformanceUtil.sleepq(50);
               sendMethod.accept(new Candy());
            }
         });
      }
      PerformanceUtil.sleepq(20);

   }

   private static Consumer<Candy> getSendMethod(TableModel model) {
      class Hack implements ICandyDataContainer {
         private ICandyDataContainerListener x;

         @Override
         public void addCandyDataContainerListener(ICandyDataContainerListener currentContainerListener) {
            this.x = currentContainerListener;
         }
         public void activate() {
         }

         public void forTestSend(Candy candy) {
            x.newData(candy);
         }
      }
      Hack hack = new Hack();
      model.handleContainerSelectionChanged(null, hack);
      return hack::forTestSend;
   }

}
