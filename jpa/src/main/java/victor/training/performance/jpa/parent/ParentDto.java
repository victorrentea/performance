package victor.training.performance.jpa.parent;

import static java.util.stream.Collectors.joining;

public record ParentDto(Long id, String name, String childrenNames) { // smells like JSON
    public static ParentDto fromEntity(Parent parent) {
      String childrenNames = parent.getChildren().stream() // ===> +1 query pt fiecare parinte
          .map(Child::getName)
          .sorted()
          .collect(joining(","));
      return new ParentDto(parent.getId(), parent.getName(), childrenNames);
    }
  }