package victor.training.performance.primitives.candy;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for identifying a Candy
 *
 */
@FunctionalInterface
public interface ICandyClassificationHandler {

	/**
	 * Identifies the Signal of the input signal row model, and updates the data model
	 *  @param callback
	 * @param candyForClassification
	 *
	 * @return
	 */
	CompletableFuture<CandyWithClassification> handleIdentification(Candy candyForClassification);

}
