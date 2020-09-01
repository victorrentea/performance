package victor.training.performance;

import static victor.training.performance.ConcurrencyUtil.log;
import static victor.training.performance.ConcurrencyUtil.sleepq;

public class ThreadLocals {

	static ThreadLocal<Integer> val = ThreadLocal.withInitial(() -> 0);

	public static void m() {
		val.set(val.get() + 1);
		sleepq(100);
		System.out.println(val.get());

	}

	public static void main(String[] args) {

		new Thread(ThreadLocals::m).start();
		new Thread(ThreadLocals::m).start();







		new RecordController().m(1, "new", "gigel");
	}
}

class UserContextHolder {
	private static ThreadLocal<String> val= new ThreadLocal<>();

	public static void setCurrentUserName(String username) {
		val.set(username);
	}

	public static String getCurrentUserName() {
		return val.get();
	}

	public static void removeUserName() {
		val.remove();
	}
	// TODO
}

// -- WARNING: enterprise code below --

class RecordController {
	private RecordFacade facade = new RecordFacade(); // fake @Autowired

	void m(int id, String newName, String username) {
		log("Acting user: " + username);

		UserContextHolder.setCurrentUserName(username);
		try {
			facade.m(id, newName);
		} finally {
			UserContextHolder.removeUserName();
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
		String username = UserContextHolder.getCurrentUserName(); // TODO
		// down in the basement
		log("INSERT INTO RECORD(..., LAST_MODIFIED_BY) VALUES (..., ?) : " + username);
	}

}