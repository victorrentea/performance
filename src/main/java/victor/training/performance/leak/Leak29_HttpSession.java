package victor.training.performance.leak;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.leak.obj.Big1KB;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

@Slf4j
@RestController
@RequestMapping("leak29")
@RequiredArgsConstructor
public class Leak29_HttpSession {
	private final UserSession userSession;

  // Auto-create sessions for anonymous accesses? Eg: store display prices in a map+ for 30 minutes
	@GetMapping
	public String usingSessionScope() {
		if (userSession.getUserPreferences() == null) {
			userSession.setUserPreferences(loadUserPreferencesFromDb());
		}

		String settingsAsString = userSession.getUserPreferences().getSettings().stream()
			.map(Big1KB::getLargeString)
			.collect(joining("<br>"));

		return "Subtle, hard to find before stress tests.<br>Try 4000 concurrent users with jMeter.<br> " +
				 "User Settings: <br>" + settingsAsString;
	}

	@GetMapping("old")
	public String usingHttpSession(HttpServletRequest request) {
		HttpSession session = request.getSession();

		UserPreferences userPreferences;
		if (session.isNew()) {
			userPreferences = loadUserPreferencesFromDb();
			session.setAttribute("lastSearchResults", userPreferences);
		} else {
			userPreferences = (UserPreferences )session.getAttribute("lastSearchResults");
		}
		return userPreferences.getSettings().stream()
			.map(Big1KB::getLargeString)
			.collect(joining("<br>"));
	}

	private UserPreferences loadUserPreferencesFromDb() {
		log.debug("Loading preferences from database");
		return new UserPreferences();
	}
}

class UserPreferences {
	private List<Big1KB> settings = new ArrayList<>();
	public UserPreferences() {
		for (int i = 0; i < 100; i++) settings.add(new Big1KB());
	}
	public List<Big1KB> getSettings() {
		return settings;
	}
}

@Data
@Component
@Scope(scopeName = "session", proxyMode = TARGET_CLASS)
class UserSession implements Serializable {
	private UserPreferences userPreferences;
}

/**
 * KEY POINTS
 * - REST services should be stateless (no user session). Consider pushing session data in A) browser B) shared redis C) DB
 * - If request authentication is based on JWT tokens, you should disable completely the session from web config
 */