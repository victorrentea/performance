package victor.training.performance.leaks;

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
//		session.setAttribute("ca-porcu",new BigObject80MB());

		// NU PUI CA PORCU pe sesiune.
		// in microservicii nu pui NIMIC pe sesiune (pt Load balancing).
		// REST care e stateless. D-aia s-a inventat JWT/SAML semnat ca sa care si drepturi
		// sa nu fii tu nevoit sa faci +1 query / fiecare request
		return "even more subtle";
	}
}
