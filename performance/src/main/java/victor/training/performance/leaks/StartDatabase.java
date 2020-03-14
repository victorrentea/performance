package victor.training.performance.leaks;

import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.sql.SQLException;

public class StartDatabase {
	public static void main(String[] args) throws SQLException {
		deletePreviousDBContents();

		System.out.println("Check out folder: ~/source/db/database/db");
		System.out.println("Started DB...");
		//hsqldb does not support Nested Transactions (REQUIRES_NEW).
//		org.hsqldb.server.Server.main("--database.0 mem:test --dbname.0 test".split(" "));

		// H2 does :)
		org.h2.tools.Server.createTcpServer().start();

	}

	private static void deletePreviousDBContents() {
		File userHomeFolder = new File(System.getProperty("user.home"));
		if (!userHomeFolder.isDirectory()) {
			throw new IllegalArgumentException("Could not locate userHome");
		}
		System.out.println("Assuming user home folder is: " + userHomeFolder.getAbsolutePath());
		File databasePath = new File(userHomeFolder, "source/db");
		if (databasePath.isDirectory()) {
			System.out.println("Deleting previous db contents...");
			boolean ok = FileSystemUtils.deleteRecursively(databasePath);
			if (!ok) {
				System.err.println("Could not delete folder " + databasePath.getAbsolutePath());
			} else {
				System.out.println("SUCCESS");
			}
		}
	}
}
