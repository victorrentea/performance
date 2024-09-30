package victor.training.performance.jpa;

import victor.training.performance.jpa.entity.Child;
import victor.training.performance.jpa.entity.Parent;

import static java.util.stream.Collectors.joining;

public record ParentDto(Long id, String name, String childrenNames) { // returned as JSON

    public static ParentDto fromEntity(Parent parent) {
      String childrenNames = parent.getChildren().stream()
          .map(Child::getName)
          .sorted()
          .collect(joining(","));
      return new ParentDto(parent.getId(), parent.getName(), childrenNames);
    }
  }