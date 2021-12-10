package victor.training.performance.primitives.candy;

import victor.training.performance.util.PerformanceUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Implementation of {@link ICandyClassificationHandler}
 */
public class CandyClassificationHandler implements ICandyClassificationHandler {

	@Override
	public void handleIdentification(Candy candyForClassification,
			Consumer<List<String>> updateCandyWithClassifications) {


		CompletableFuture<List<String>> classificationFuture = CompletableFuture.supplyAsync(() ->
			findMatchingCandies(candyForClassification));

		classificationFuture.thenAccept(updateCandyWithClassifications);

	}


	private List<String> findMatchingCandies(Candy candyForClassification) {



		PerformanceUtil.cpu(10);
		return List.of("sweet","fat");
	}

}
