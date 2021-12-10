package victor.training.performance.primitives.candy;

import lombok.extern.slf4j.Slf4j;
import victor.training.performance.util.PerformanceUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO - Insert class documentation here
 */
@Slf4j // @Service @Bean singlton
public class TableDataModel {

	public void addNewData(List<Candy> list) {
		// TODO Auto-generated method stub
	}
	public void removeAllData() {
		// TODO Auto-generated method stub
	}

	private static final AtomicInteger currentCalls = new AtomicInteger(0);

	public void updateData(CandyWithClassification cwc) {

		if (currentCalls.incrementAndGet() > 1) {
			System.err.println("BANG!  UI CRASH");
			System.exit(-99);
		}
		log.debug(cwc.getCandy() + " was classified to " + cwc.getClassifications());
		PerformanceUtil.sleepq(100);
		log.debug(cwc.getCandy() + "done printing");
		currentCalls.decrementAndGet();
	}

}
