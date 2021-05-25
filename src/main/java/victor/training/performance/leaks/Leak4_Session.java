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
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Slf4j
@RestController
@RequestMapping("leak4")
@RequiredArgsConstructor
// Suffocated heap example
public class Leak4_Session {
	private final UserSession userSession;


	@GetMapping
	public String test(HttpServletRequest request) {
		HttpSession session = request.getSession();

		List<BigObject1KB> list;
		if (session.isNew()) {
			list = retrieveUserPreferences();
			session.setAttribute("lastSearchResults", list);
		} else {
			list = (List<BigObject1KB>) session.getAttribute("lastSearchResults");
		}

		String listStr = list.stream().map(BigObject1KB::getLargeString).collect(joining("<br>"));
		return "Subtle, hard to find before stress tests.<br>Try 4000 concurrent users with jMeter.<br> Last Search Results: <br>" + listStr;
	}

	private List<BigObject1KB> retrieveUserPreferences() {
		log.debug("Perform the search");
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