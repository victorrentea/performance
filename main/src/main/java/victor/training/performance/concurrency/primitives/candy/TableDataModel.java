package victor.training.performance.concurrency.primitives.candy;

import lombok.extern.slf4j.Slf4j;
import victor.training.spring.batch.util.PerformanceUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO - Insert class documentation here
 */
@Slf4j
public class TableDataModel {

	public void addNewData(List<Candy> list) {
		// TODO Auto-generated method stub
	}
	public void removeAllData() {
		// TODO Auto-generated method stub
	}

	private static final AtomicInteger currentCalls = new AtomicInteger(0);

	public void updateData(Candy candy, final List<String> classes) {

		if (currentCalls.incrementAndGet() > 1) {
			System.exit(-99);
		}
		log.debug(candy + " was classified to " + classes);
		PerformanceUtil.sleepMillis(10);
		currentCalls.decrementAndGet();
	}

}
