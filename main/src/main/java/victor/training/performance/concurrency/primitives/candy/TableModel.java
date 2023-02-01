package victor.training.performance.concurrency.primitives.candy;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;


public class TableModel {
   private final ExecutorService updateExecutor;
   private final ICandyClassificationHandler candyClassificationHandler;

   private TableDataModel dataModel;

   private ICandyDataContainerListener currentContainerListener;

   public TableModel(
       ICandyClassificationHandler candyClassificationHandler,
       ExecutorService candyClassificationUpdateExecutor) {

      this.candyClassificationHandler = requireNonNull(candyClassificationHandler);
      this.updateExecutor = requireNonNull(candyClassificationUpdateExecutor);
      this.currentContainerListener = createContainerListener();
   }

   private ICandyDataContainerListener createContainerListener() {
      return new ICandyDataContainerListener() {
         @Override
         public void newData(Candy candy) {
            dataModel.addNewData(Collections.singletonList(candy));
            updateCandyRowModelWithClassifications(candy);
         }
         @Override
         public void removeAll() {
            // do similar things like add
         }
         @Override
         public void updateData(Candy updatedCandy, Candy oldCandy) {
         }
         @Override
         public void removeData(Candy sandy) {
         }

         @Override
         public void removeData(Collection<Candy> candys) {
         }

      };
   }
   public void setDataModel(TableDataModel dataModel) {
      this.dataModel = dataModel;
   }

   public void handleContainerSelectionChanged(ICandyDataContainer oldContainer, ICandyDataContainer newContainer) {
      System.out.println("TODO Unregister old container " + oldContainer);
      dataModel.removeAllData();

      registerContainer(newContainer);
   }

   // user controls the change of container.
   private void registerContainer(ICandyDataContainer container) { /// the container contains data sources
      container.activate();
      currentContainerListener = createContainerListener();
      container.addCandyDataContainerListener(currentContainerListener);
   }

   // ! 10k / minute  for each new candy
   private void updateCandyRowModelWithClassifications(Candy candy) {
      Consumer<List<String>> callback = classifications -> {
         synchronized (TableModel.this) {
            dataModel.updateData(candy, classifications);
         }
      };

      updateExecutor.execute(() -> candyClassificationHandler.handleIdentification(candy, callback));
   }
}
