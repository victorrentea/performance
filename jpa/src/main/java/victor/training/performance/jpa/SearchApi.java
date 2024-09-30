package victor.training.performance.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.jpa.entity.Parent;
import victor.training.performance.jpa.repo.ParentSearchRepo;


@Slf4j
@RestController
@RequiredArgsConstructor
public class SearchApi {
  private final ParentSearchRepo parentSearchRepo;

  @GetMapping("search")
  public Page<Parent> searchPaginated(
      @RequestParam(defaultValue = "ar") String q,
      @RequestParam(defaultValue = "0") int pageIndex,
      @RequestParam(defaultValue = "20") int pageSize,
      @RequestParam(defaultValue = "name") String order,
      @RequestParam(defaultValue = "ASC") String dir
  ) {
    PageRequest pageRequest = PageRequest.of(pageIndex, pageSize, Direction.fromString(dir), order);

    return parentSearchRepo.searchByNameLike("%" + q + "%", pageRequest);
    // TODO #1 - start the JpaApp and inspect response of http://localhost:8080/search
    //   then look in the logs to see the number of queries executed

    // TODO #2 - return a list of ParentDto mapped from entities ; commit
    //   reduce the number of DB queries using @BatchSize ; commit

    // TODO #3 - use the driving query technique
    //   1. use #findIdsPage to identify which Parents match the criteria
    //   2. use #fetchParentsByIds to load Parents with children
    //   commit;

    // TODO #4 - write a @Query method that select from ParentView or ParentSubselect instead of Parent; commit
    //   how many queries are executed now? ; commit

    // TODO #5 (bonus) - add a new optional search criteria: country name (exact match, case-insensitive)
    //  The query should include only the provided criteria:
    //  - ?q=t1x&country=country2 -> returns all Parents with name containing 't1x' and country 'country2'
    //  - ?q=t1x -> returns all Parents with name containing 't1x' regardless of country
    //  Hint: inspiration: victor.training.performance.jpa.repo.UberRepo.searchFixedJqpl
  }

}

