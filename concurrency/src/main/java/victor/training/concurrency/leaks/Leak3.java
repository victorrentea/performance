package victor.training.concurrency.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("leak3")
public class Leak3 {

	@GetMapping
	public String test(HttpServletRequest request) {
		// Clear JSESSIONID cookie
		HttpSession session = request.getSession();
		System.out.println("was new session: " + session.isNew());
		session.setAttribute("rights",new BigObject80MB());
		return "even more subtle";
	}
}
