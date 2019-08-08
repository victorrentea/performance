package victor.training.concurrency.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("leak5")
public class Leak5 {
	
	@GetMapping
	public String root() throws Exception {
		return "call <a href='./leak5/one'>/one</a> and <a href='./leak5/two'>/two</a> withing 3 secs..";
	}
	
	@GetMapping("/one")
	public String one() throws Exception {
		KillOne.entryPoint();
		return "--> You didn't call /two within the last 3 secs, didn't you?..";
	}
	
	@GetMapping("/two")
	public String two() throws Exception {
		KillTwo.entryPoint();
		return "--> You didn't call /one within the last 3 secs, didn't you?..";
	}
}
