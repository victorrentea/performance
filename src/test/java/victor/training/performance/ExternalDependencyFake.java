package victor.training.performance;

import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalDependencyFake implements ExternalDependency {
   private final int total;

   public ExternalDependencyFake(int total) {
      this.total = total;
   }

   @Override
   public boolean isAlive(int id) {
      return id % 2 == 0;
   }

   private boolean overlappingEmails = false;
   private boolean checkingEmails = false;

   public ExternalDependencyFake withOverlappingEmails() {
      this.overlappingEmails = true;
      return this;
   }

   public ExternalDependencyFake withCheckingEmails() {
      this.checkingEmails = true;
      return this;
   }

   public String retrieveEmail(int i) {
      int emailId = i;
      if (overlappingEmails) {
         emailId %= total / 2;
      }
      return "email" + emailId + "@example.com";
   }


   private final AtomicInteger emailChecksCounter = new AtomicInteger(0);
   private static final Pattern emailPattern = Pattern.compile("email(\\d+)@example.com");

   @Override
   public boolean checkEmail(String email) {
      Matcher matcher = emailPattern.matcher(email);
      if (!matcher.matches()) throw new IllegalArgumentException();
      int number = Integer.parseInt(matcher.group(1));

      emailChecksCounter.incrementAndGet();
      PerformanceUtil.sleepSomeTime(0, 1);
      if (!checkingEmails) return true;
      else return number % 2 == 0;
   }
   public int emailChecksPerformed() {
      return emailChecksCounter.get();
   }
}
