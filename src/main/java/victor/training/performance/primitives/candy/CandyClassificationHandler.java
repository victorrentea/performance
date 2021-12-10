package victor.training.performance.primitives.candy;

import lombok.Value;
import victor.training.performance.util.PerformanceUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of {@link ICandyClassificationHandler}
 */
public class CandyClassificationHandler implements ICandyClassificationHandler {

	@Override
	public CompletableFuture<CandyWithClassification> handleIdentification(Candy candy) {
		return CompletableFuture.supplyAsync(() -> new CandyWithClassification(candy, findMatchingCandies(candy)));
	}


	private List<String> findMatchingCandies(Candy candyForClassification) {
		PerformanceUtil.cpu(10);
		return List.of("sweet","fat");
	}
}

@Value
class CandyWithClassification {
	Candy candy;
	List<String> classifications;
}