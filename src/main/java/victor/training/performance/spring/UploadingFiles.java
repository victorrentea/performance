package victor.training.performance.spring;

import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.LobCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.UUID;

@RestController
public class UploadingFiles {

   @Transactional
   @PostMapping("upload")
   public String uploadFile(@RequestParam MultipartFile upload) throws IOException {

      String uploadId = UUID.randomUUID().toString();
      File tempFile = File.createTempFile("upload-"+uploadId, "dat");

      try {
         try (FileOutputStream tempOutputStream = new FileOutputStream(tempFile)) {
            IOUtils.copy(upload.getInputStream(), tempOutputStream);
         }
         // aici:
         // 1) trimiti fisier cuiva prin FTP/CFT/NAS
         // 2) procesezi tot fisierul dar NU aici: a) @Async sau submit() pasand uploadId b) insert into FILE_TO_PROCESS (username, uploadId, timestamp, TO_PROCESS) c) cel mai proofy: iti dai singur mesaj pe coada si il procesezi cu Spring Batch (>100 MB)
         // 3) uploadezi fisierul in DB!

//JDBC
//         try (FileOutputStream tempInputStream = new FileOutputStream(tempFile)) {
//            PreparedStatement ps = conn.prepareStatemet("INSERT INTO () VALUES (?, ?)");
//            ps.setBlob(2, tempInputStream);
//            ps.executeUpdate();
//         }


         Session hibernateSession = (Session) entityManager.getDelegate();
         LobCreator lobCreator = Hibernate.getLobCreator(hibernateSession);

         InputStream fileInputStream = new FileInputStream(tempFile);
         Blob blob = lobCreator.createBlob(fileInputStream, tempFile.length());

         entityManager.persist(new FileData().setUploadId(uploadId).setFile(blob));
         // o sa ai o eroare: caci faci delete la fisier mai jos in finally inainte ca sa faca Hib efectiv INSERTUL
         // a) reproiectezi sa faci delete in exteriorul functiei
         // b) TransactionEventListener https://blog.pragmatists.com/spring-events-and-transactions-be-cautious-bdb64cb49a95

      } finally {
         tempFile.delete();
      }
      return "Am primit fisierul, uploadID:"+uploadId;
   }

   @GetMapping("download")
   public void download(@RequestParam String downloadId, HttpServletResponse response) throws SQLException, IOException {

      // JDBC sau Hibernate
//      ResultSet rs;
//      Blob blob = rs.getBlob(2);
//      IOUtils.copy(blob.getBinaryStream(), response.getOutputStream());
//
//      InputStream inputStream = new GZIPInputStream(blob.getBinaryStream());
//
//      JsonFactory jfactory = new JsonFactory();
//      JsonParser jParser = jfactory.createParser(inputStream);
 // cauta si chestii mai cool de tranformare : "jq"

//      XSLT
//      jParser.next


   }

   @Autowired
   EntityManager entityManager;
}
// <form>  <input type="file" name="upload">
@Data
@Entity
class FileData {
   @Id
   private String uploadId;
   @Lob
   private Blob file;
}