package victor.training.performance;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

import static victor.training.performance.PerformanceUtil.log;
import static victor.training.performance.PerformanceUtil.sleepq;

@Slf4j
public class ThreadLocals {
public static final Random r = new Random();
	public static final ThreadLocal<String> variabileLinkateCuThreadul =new ThreadLocal<>();
	//1) cel mai sigur loc de tinut date PRIVATE threadului tau ca sa nu existe race bugs:   JDBCConnection
	//2) pt a pasa magic metadate de request
	public static void main(String[] args) {
		for (int i =1;i<3; i++) {
			new Thread(
			() -> {
				variabileLinkateCuThreadul.set("Un string " + Thread.currentThread().getName());
				sleepq(1000);
				String s = variabileLinkateCuThreadul.get();
				log.info("valoarea mea este " + s);
			}).start();
		}

		new RecordController().m(1, "new", "gigel");
	}
}











//@Scope("request")
class UserContextHolder {
	public static final ThreadLocal<String> currentUsername = new ThreadLocal<>();
}

// -- WARNING: enterprise code below --

class RecordController {
	private RecordFacade facade = new RecordFacade(); // fake @Autowired

	void m(int id, String newName, String username) {
		log("Acting user: " + username);
		UserContextHolder.currentUsername.set(username);
		try {
			facade.m(id, newName);
		} finally {
			UserContextHolder.currentUsername.remove();
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
		String username = UserContextHolder.currentUsername.get(); // TODO
		// down in the basement
		log("INSERT INTO RECORD(..., LAST_MODIFIED_BY) VALUES (..., ?) : " + username);
	}

}