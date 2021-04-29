package victor.training.performance.leaks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("leak2")
@RequiredArgsConstructor
public class Leak2 {
	private final UserSession userSession;

	@GetMapping
	public String test(HttpServletRequest request) {
		// to test, use 2 browsers or clear JSESSIONID cookie
		HttpSession session = request.getSession();

		if (session.isNew()) {
			log.debug("Filling the new session");
			List<BigObject1KB> list = getUserPreferences();
			session.setAttribute("screenState", list);
		} else {
			List<BigObject1KB> list = (List<BigObject1KB>) session.getAttribute("screenState");
			log.debug("List " + list);
		}

//		session.setAttribute("userRights",new BigObject80MB());
		return "subtle, hard to find before stress tests. Try 4000 concurrent users + measure";
	}

	private List<BigObject1KB> getUserPreferences() {
		List<BigObject1KB> list = new LinkedList<>();
		for (int i = 0; i < 100; i++) {
			list.add(new BigObject1KB());
		}
		return list;
	}
}


@Component
@Scope(scopeName = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
class UserSession implements Serializable {



}