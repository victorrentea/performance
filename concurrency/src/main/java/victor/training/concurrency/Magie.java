package victor.training.concurrency;

public class Magie {

	public static void main(String[] args) {
	new RecordController().m(1,"new", "gigel");
	}
}
class UserContextHolder {
	private static ThreadLocal<String> usernameHolder = ThreadLocal.withInitial(() -> "noname");
	public static String getCurrentUsernameFromCurrentThread() {
		return usernameHolder.get();
	}
	public static void setCurrentUsernameOnCurrentThread(String username) {
		usernameHolder.set(username);
	}
}

class RecordController {
	RecordFacade facade = new RecordFacade();

	void m(int id, String newName, String username) {
//		SpringSecurityContextHolder.
		UserContextHolder.setCurrentUsernameOnCurrentThread(username);
		facade.m(id, newName);
	}
}

class RecordFacade {
	RecordService service = new RecordService();

	void m(int id, String newName) {
//		SpringSecurityContextHolder.
		service.m(id, newName);
	}
}

class RecordService {
	RecordRepo repo = new RecordRepo();

	void m(int id, String newName) {
		repo.updateRecord(id, newName);
	}
}


class RecordRepo {


	void updateRecord(int recordId, String newName) {
		String username = hocusPocus();
		// pun in insert si userul curent
		System.out.println("INSERT INTO ... " + username);
	}

	private String hocusPocus() {
		return UserContextHolder.getCurrentUsernameFromCurrentThread();
	}
}