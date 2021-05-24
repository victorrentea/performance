package victor.training.performance.assignment.search;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("car")
@RequiredArgsConstructor
public class SearchController {
    private final SearchRepo repo;

    @PostMapping("search")
    public List<Car> searchCar(@RequestBody CarSearchCriteria searchCriteria) {
        return repo.search(searchCriteria);
    }

}
