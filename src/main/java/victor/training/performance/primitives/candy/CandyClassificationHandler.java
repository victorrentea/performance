package victor.training.performance.primitives.candy;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Implementation of {@link ICandyClassificationHandler}
 */
public class CandyClassificationHandler implements ICandyClassificationHandler {



	@Override
	public void handleIdentification(
			final Candy candyForClassification,
			final Consumer<List<String>> updateCandylWithClassifications) {


		final CompletableFuture<List<String>> classificationFuture = callServiceToClassifyTheCandy(candyForClassification);

		classificationFuture.thenAccept(updateCandylWithClassifications);

	}

	private CompletableFuture<List<String>> callServiceToClassifyTheCandy(final Candy candyForClassification) {

		final CompletableFuture<List<String>> completableFuture = CompletableFuture
				.supplyAsync(() -> findMatchingCandies(candyForClassification));

		return completableFuture;
	}


	private List<String> findMatchingCandies(final Candy candyForClassification) {
		// do heavy stuff CPU
		return null;
	}

}
