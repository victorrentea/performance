package victor.training.performance.concurrency.primitives.candy;

import victor.training.spring.batch.util.PerformanceUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class CandyClassificationHandler implements ICandyClassificationHandler {
	@Override
	public void handleIdentification(Candy candy, Consumer<List<String>> updateCandyWithClassifications) {
		CompletableFuture<List<String>> classificationFuture =
			CompletableFuture.supplyAsync(() -> findMatchingCandies(candy));

		classificationFuture.thenAccept(updateCandyWithClassifications);
	}

	private List<String> findMatchingCandies(Candy candyForClassification) {
		PerformanceUtil.cpu(10);
		return List.of("sweet","fat");
	}

}
