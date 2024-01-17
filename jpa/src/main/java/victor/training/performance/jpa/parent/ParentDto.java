package victor.training.performance.jpa.parent;

import static java.util.stream.Collectors.joining;

public record ParentDto(Long id, String name, String childrenNames) { // smells like JSON
    public static ParentDto fromEntity(Parent parent) {
      // JPA nu e prost. El incearca sa evite
      // sa incarce toti copiii din prima, ca poate nu ai nevoie de ei.

      // cand scoate din DB un Parent lasa lista de copii neincarcata.

      //la primul aces la colectie, JPA se va duce in DB sa-i aduca
      String childrenNames = parent.getChildren().stream()
          .map(Child::getName)
          .sorted()
          .collect(joining(","));
      return new ParentDto(parent.getId(), parent.getName(), childrenNames);
    }
  }