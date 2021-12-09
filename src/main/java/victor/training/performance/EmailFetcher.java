package victor.training.performance;

import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailFetcher {
   private final int total;
   private boolean overlappingEmails = false;

   public EmailFetcher(int total) {
      this.total = total;
   }

   public EmailFetcher withOverlappingEmails() {
      this.overlappingEmails = true;
      return this;
   }

   /** Imagine some some external call */
   public String retrieveEmail(int i) {
      int emailId = i;
      if (overlappingEmails) {
         emailId %= total / 2;
      }
      return "email" + emailId + "@example.com";
   }

   public AtomicInteger emailChecksCounter = new AtomicInteger(0);
   private static final Pattern emailPattern = Pattern.compile("email(\\d+)@example.com");

   /** Imagine some some external call */
   public boolean checkEmail(String email) {
      Matcher matcher = emailPattern.matcher(email);
      if (!matcher.matches()) throw new IllegalArgumentException();
      int number = Integer.parseInt(matcher.group(1));

      emailChecksCounter.incrementAndGet();
      PerformanceUtil.sleepSomeTime(0, 1);
      return number % 2 == 0;
   }
}
