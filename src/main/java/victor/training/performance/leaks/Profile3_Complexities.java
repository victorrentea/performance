package victor.training.performance.leaks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.interview.Hashing;

@Slf4j
@RestController
@RequestMapping("/profile/cpu")
@RequiredArgsConstructor
public class Profile3_Complexities {
   private final SmallRepo smallRepo;

   @GetMapping
   public void profileMe() {
      Hashing.intersectCollections();
   }
}
