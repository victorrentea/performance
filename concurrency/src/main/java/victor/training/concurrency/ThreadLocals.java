package victor.training.concurrency;

import static victor.training.concurrency.ConcurrencyUtil.log;

public class ThreadLocals {

	public static void main(String[] args) {
		new Thread(() -> new RecordController().m(1, "new", "gigel")).start();
		new Thread(() -> new RecordController().m(1, "new", "maricica")).start();
	}
}

// palarie magica
class UserContextHolder {
	private static String varza;
	private static ThreadLocal<String> usernameulDePeThread = new ThreadLocal<String>();
	
	public static void setCurrentUser(String username) {
		usernameulDePeThread.set(username);
//		varza = username;
	}
	public static String getCurrentUser() {
		return usernameulDePeThread.get();
//		return varza;
	}
}



// -- WARNING: enterprise code below --

class RecordController {
	private RecordFacade facade = new RecordFacade(); // fake @Autowired

	void m(int id, String newName, String username) {
		log("Acting user: " + username);
		UserContextHolder.setCurrentUser(username);
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

// BECI
class RecordRepo {
	void updateRecord(int recordId, String newName) {
		ConcurrencyUtil.sleepSomeTime();
		String username = UserContextHolder.getCurrentUser(); // TODO
		// down in the basement
		log("INSERT INTO RECORD(..., LAST_MODIFIED_BY) VALUES (..., ?) : " + username);
	}

}