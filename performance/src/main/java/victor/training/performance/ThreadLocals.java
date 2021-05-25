package victor.training.performance;

import static victor.training.performance.PerformanceUtil.log;

public class ThreadLocals {

	public static void main(String[] args) {

		new Thread(() -> new RecordController().m(1, "new", "george")).start();
		new Thread(() -> new RecordController().m(1, "new", "djordje")).start();

	}
}

class UserContextHolder {
	// TODO
	public static ThreadLocal<String> currentUsername = new ThreadLocal<>();
}

// -- WARNING: enterprise code below --

class RecordController {
	private RecordFacade facade = new RecordFacade(); // fake @Autowired

	void m(int id, String newName, String username) {
		UserContextHolder.currentUsername.set(username);
		log("Acting user: " + username);
		facade.m(id, newName);
	}
	void fireRockets(int id, String newName, String username) {
//		UserContextHolder.currentUsername.set(username);
		log("Acting user: " + username);
		facade.m(id, newName);
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
		String username = UserContextHolder.currentUsername.get();
		// down in the basement
		log("INSERT INTO RECORD(..., LAST_MODIFIED_BY) VALUES (..., ?) : " + username);
	}

}