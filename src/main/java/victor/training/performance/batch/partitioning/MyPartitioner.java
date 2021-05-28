package victor.training.performance.batch.partitioning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;
@Slf4j
public class MyPartitioner implements Partitioner {
   @Override
   public Map<String, ExecutionContext> partition(int gridSize) {
      Map<String, ExecutionContext> map = new HashMap<>();
      for (int i = 0; i < gridSize; i++) {
         ExecutionContext onePartitionContext = new ExecutionContext();
         onePartitionContext.put("MOD", i);
         String partitionName = "Person%" + gridSize + "=" + i;
         map.put(partitionName, onePartitionContext);
      }
      log.info("Created paritions: " + map);
      return map;
   }
}
