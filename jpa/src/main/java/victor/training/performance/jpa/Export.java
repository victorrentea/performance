package victor.training.performance.jpa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import victor.training.performance.jpa.entity.Parent;
import victor.training.performance.jpa.repo.ParentRepo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

@Slf4j
@Service
@RequiredArgsConstructor
public class Export {
  private final ParentRepo parentRepo;
  private final EntityManager entityManager;

  public void export() {
    try (Writer writer = new BufferedWriter(new FileWriter("export.txt"))) {
      for (Parent parent : parentRepo.findAll()) {
        writer.write(parent.toString() + "\n");
      }
    } catch (IOException e) {
      log.error("Failed to export", e);
    }
  }
}
