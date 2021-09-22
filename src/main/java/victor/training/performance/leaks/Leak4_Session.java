package victor.training.performance.leaks;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public String test() {
//		HttpSession s;
//		s.setAttribute("a", new Obj() implements Serializable); // JavaEE
		List<BigObject1KB> list;
		if (userSession.getLastSearchResults()== null) {
			list = retrieveUserPreferences();
			userSession.setLastSearchResults(list);
		} else {
			list = userSession.getLastSearchResults();
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

		// DACA ai de tinut in sesiune, cat sa tii ?
		//1: tot ce tii in session.setAttribute sau @Scope(session) mare grija sa economisesti memoria
		   // int[], int nu long, 3 campuri nu 20,
		// 2: daca ai mult de stocat in sesiune vezi cum faci sa scapi de date din memorie
		   // idee: in fiere, in DB, Redis

		return list;
	}
}


@Data
@Component
@Scope(scopeName = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
class UserSession implements Serializable {
	List<BigObject1KB> lastSearchResults;
//	10MB x 1000 useri simultani conectati = 10G
	// HTML pe server side
	// List<Result> toate; //10K
	// dar in UI ii randezi pagina 2 adica toate.subList(50,100)

	// SELECT * FROM X LIMIT 50 OFFSET 50
	// daca nu e DB de unde iau: le iau pe toate o data :
	// a) [best] le trimit pe toate in FE (daca incap)
	//b) le paginez din memoria REDIS (alta masina)


}