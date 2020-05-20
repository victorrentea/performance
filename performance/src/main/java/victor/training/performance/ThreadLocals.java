package victor.training.performance;

import javax.swing.*;

import static victor.training.performance.ConcurrencyUtil.log;

public class ThreadLocals {

	public static void main(String[] args) {
		new Thread(() -> new RecordController().m(1, "new", "gigel")).start();
		new Thread(() -> new RecordController().m(1, "new", "maria")).start();

	}
}

//@Scope("request") sau @Scope("thread")
class UserContextHolder {
	private static final ThreadLocal<String> currentUserName = new ThreadLocal<>();

	public static void setUserOnThread(String username) {
		currentUserName.set(username);
	}

	public static String getUserFromThread() {
		SwingUtilities.invokeLater(() -> {/*doar de aici ai voie sa modifici componente de Swing*/});
		return currentUserName.get();
	}

	public static void clearUserFromThread() {
		currentUserName.remove();
	}
}

// -- WARNING: enterprise code below --

class RecordController {
	private RecordFacade facade = new RecordFacade(); // fake @Autowired

	void m(int id, String newName, String username) {
//		SecurityContextHolder.getContext(); // la fel face si el?
		log("Acting user: " + username);
		UserContextHolder.setUserOnThread(username);
		try {
			facade.m(id, newName);
		} finally {
			UserContextHolder.clearUserFromThread();
		}
	}
}

class RecordFacade {
	private RecordService service = new RecordService(); // fake @Autowired

	void m(int id, String newName) {
		service.m(id, newName);
	}
}

class RecordService {
	private RecordRepo repo = new RecordRepo(); // fake @Autowired

	void m(int id, String newName) {
		repo.updateRecord(id, newName);
	}
}

class RecordRepo {
	void updateRecord(int recordId, String newName) {
		String username = UserContextHolder.getUserFromThread(); // TODO
		// down in the basement
		log("INSERT INTO RECORD(..., LAST_MODIFIED_BY) VALUES (..., ?) : " + username);
	}

}