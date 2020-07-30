package victor.training.performance.leaks;

import org.apache.commons.io.IOUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@RestController
@RequestMapping("leak10")
public class Leak10_FileUpload {
	@GetMapping
	public String test(HttpServletRequest request) throws IOException {

		File tempFile = File.createTempFile("temp", "dat"); // creates file in /tmp -- careful with quotas - to have space

		try (FileOutputStream tempStream = new FileOutputStream(tempFile)) {
			IOUtils.copy(request.getInputStream(), tempStream);
		}
		System.out.println("Write to DB that a file named " + tempFile.getAbsolutePath() +" is ready to upload (status=PENDING)");
		long uploadId = 99L; // persist gives you an id

		return "Got your file. It will be processed soon. Please check upload id " + uploadId; // diplayed to user in UI
		//Probably user has a screen to see the statuses of his upload by Id.
	}
}

// everything below is actually by default implemented by Spring Batch
@Component
class FileProcessor {
//	@Scheduled(fixedDelay = 1000)
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void m() {
		System.out.println("Look into DB for uploads to process WHERE STATUS='PENDING'");
		try {
			System.out.println("update set STATUS='STARTED'"); // anti-paralelism
			//1) Spring Scheduled doesn't re-enter method while running
			//2) idea: upload file on a NAS (shared file system) and have multiple instances look at that location


			System.out.println("update set STATUS='COMPLETED'");
		} catch (Exception e) {
			System.out.println("update set STATUS='ERROR'");
			// WARNING: toate scrierile in DB trebuie facute pe tranzactii micute izolate. (REQUIRES_NEW sau te asiguri ca nu RULEZI tot intr-o trazactie)
		}
	}
}