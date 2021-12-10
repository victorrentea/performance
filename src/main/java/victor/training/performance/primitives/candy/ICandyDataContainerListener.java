package victor.training.performance.primitives.candy;

import java.util.Collection;

/**
 * TODO - Insert class documentation here
 */
public interface ICandyDataContainerListener {

	void newData(final Candy candy);

	public void updateData(final Candy updatedCandy, final Candy oldCandy);

	public void removeData(final Candy sandy);

	public void removeData(final Collection<Candy> candys);


	public void removeAll();
}
