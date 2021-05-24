package victor.training;

import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.sql.SQLException;

public class StartDatabase {
	public static void main(String[] args) throws SQLException {
		System.out.println("Started DB...");
		System.out.println("Connecting to 'jdbc:h2:tcp://localhost:9092/~/test' will auto-create a database file 'test.mv.db' in user home (UNIX:~, WIN:c:\\users\\<youruser>)...");

		// Allow auto-creating new databases on disk at first connection
		org.h2.tools.Server.createTcpServer("-ifNotExists").start();
		// MUST HAVE VER 1.4.199
	}

}
