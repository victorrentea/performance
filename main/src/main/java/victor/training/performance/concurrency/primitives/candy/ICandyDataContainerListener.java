package victor.training.performance.concurrency.primitives.candy;

import java.util.Collection;

public interface ICandyDataContainerListener {
   void newData(final Candy candy);

   void updateData(final Candy updatedCandy, final Candy oldCandy);

   void removeData(final Candy sandy);

   void removeData(final Collection<Candy> candys);

   void removeAll();
}
