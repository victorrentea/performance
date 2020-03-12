package victor.training.concurrency.leaks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("leak4")
public class Leak4 {
	@Autowired
    private UserContext userData;
	
	@GetMapping
	public String test() throws Exception {
		String uuid = UUID.randomUUID().toString();
		userData.tryCache(uuid, BigObject20MB::new);
		return "the most subtle";
	}
}
