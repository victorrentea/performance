package victor.training.performance.batch.partition;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.Map;

public class MyPartitioner implements Partitioner {
   @Override
   public Map<String, ExecutionContext> partition(int gridSize) {
      return null;
   }
}
