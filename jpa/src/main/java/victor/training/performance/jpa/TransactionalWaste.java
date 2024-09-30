package victor.training.performance.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import victor.training.performance.jpa.entity.Parent;
import victor.training.performance.jpa.repo.ParentRepo;

@Slf4j
@RestController
@RequiredArgsConstructor
@Transactional
public class TransactionalWaste {
  private final ParentRepo parentRepo;
  private final RestTemplate restTemplate = new RestTemplate();

  public record Response(String name, String review) {
  }
  @GetMapping("parent/{parentId}")
  public Response transactional(@PathVariable @DefaultValue("101") long parentId) {
    Parent parent = parentRepo.findById(parentId).orElseThrow();
    String review = restTemplate.getForObject("http://localhost:8080/external-call/" + parentId,String.class);
    return new Response(parent.getName(), review);
  }
}
