package victor.training.performance.jpa.projections;

import java.util.List;

public interface ParentProjected {
  Long getId();

  String getName();

  List<ChildProjected> getChildren();

}

