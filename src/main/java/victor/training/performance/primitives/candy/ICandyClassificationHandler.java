package victor.training.performance.primitives.candy;

import java.util.List;
import java.util.function.Consumer;

@FunctionalInterface
public interface ICandyClassificationHandler {
	void handleIdentification(Candy candy, Consumer<List<String>> updateCandyWithClassifications);

}
