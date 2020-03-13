package victor.training.performance;

import static victor.training.performance.ConcurrencyUtil.log;

public class ThreadLocals {

	public static void main(String[] args) {
		new RecordController().m(1, "new", "gigel");
	}
}

class UserContextHolder {
	// TODO
}

// -- WARNING: enterprise code below --

class RecordController {
	private RecordFacade facade = new RecordFacade(); // fake @Autowired

	void m(int id, String newName, String username) {
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
		String username = "???"; // TODO
		// down in the basement
		log("INSERT INTO RECORD(..., LAST_MODIFIED_BY) VALUES (..., ?) : " + username);
	}

}