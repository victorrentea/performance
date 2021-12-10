package victor.training.performance.primitives.candy;

import java.util.List;
import java.util.function.Consumer;

/**
 * Interface for identifying a Candy
 *
 */
@FunctionalInterface
public interface ICandyClassificationHandler {

	/**
	 * Identifies the Signal of the input signal row model, and updates the data model
	 *  @param candyForClassification
	 *
	 * @param callback
	 * @return
	 */
	void handleIdentification(Candy candyForClassification, Consumer<List<String>> updateCandylWithClassifications);

}
