package victor.training.performance.leaks;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("leak2")
public class Leak2 {

	@GetMapping
	public String test(HttpServletRequest request) {
		// Clear JSESSIONID cookie
		HttpSession session = request.getSession();
		System.out.println("Just created a new session: " + session.isNew());
//		session.setAttribute("rights",new BigObject80MB()); // nu pune chestii grele pe sesiunea cu browserele.

		// A) NU tii in memorie date specifice UNUI browser. ci le aduci din DB de fiecare data
		// >>> sa ai endpointuri stateless (default pentru microservicii : clientii tai sunt alte app).

		// B) pe un Redis/hazelcast/ (pe un alt server cu 64 GB de RAM care) raspunde ff repede <1ms fata de un DB care dureaza mult
		     /// Load Balacing

		// C) (dev magar) trimiti in Browser informatiile il loc sa le tii pe Java.
		// .. problema: securitate: userul le poate schimba pe browser.
		// >>>> TOKENS semnate de server ca sa nu le poata altera continutul browserului




		return "subtle, hard to find before stress tests";
	}
}
