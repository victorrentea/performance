package victor.perf;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 15, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class ParameterObjectsTest {

   @Benchmark
   public int primitiveParameters() {
      return m1(1L, "jdoe", "2021-02-02", 23);
   }

   private int m1(long userId, String username, String createDate, int age) {
      return m2(userId, username, createDate, age);
   }

   private int m2(long userId, String username, String createDate, int age) {
      return m3(userId, username, createDate, age);
   }

   private int m3(long userId, String username, String createDate, int age) {
      return m4(userId, username, createDate, age);
   }

   private int m4(long userId, String username, String createDate, int age) {
      return m5(userId, username, createDate, age);
   }

   private int m5(long userId, String username, String createDate, int age) {
      return m6(userId, username, createDate, age);
   }

   private int m6(long userId, String username, String createDate, int age) {
      return m7(userId, username, createDate, age);
   }

   private int m7(long userId, String username, String createDate, int age) {
      return m8(userId, username, createDate, age);
   }

   private int m8(long userId, String username, String createDate, int age) {
      return m9(userId, username, createDate, age);
   }

   private int m9(long userId, String username, String createDate, int age) {
      return m10(userId, username, createDate, age);
   }

   Random r = new Random();
   private int m10(long userId, String username, String createDate, int age) {
      return r.nextInt();
   }

   @Benchmark
   public int parameterObjects() {
      return mo1(new UserDetails(1L, "jdoe", "2021-02-02", 23));
   }

   static class UserDetails {
      private final long id;
      private final String username;
      private final String createDate;
      private final int age;

      UserDetails(long id, String username, String createDate, int age) {
         this.id = id;
         this.username = username;
         this.createDate = createDate;
         this.age = age;
      }
   }
   private int mo1(UserDetails userDetails) {
      return mo2(userDetails);
   }

   private int mo2(UserDetails userDetails) {
      return mo3(userDetails);
   }

   private int mo3(UserDetails userDetails) {
      return mo4(userDetails);
   }

   private int mo4(UserDetails userDetails) {
      return mo5(userDetails);
   }

   private int mo5(UserDetails userDetails) {
      return mo6(userDetails);
   }

   private int mo6(UserDetails userDetails) {
      return mo7(userDetails);
   }

   private int mo7(UserDetails userDetails) {
      return mo8(userDetails);
   }

   private int mo8(UserDetails userDetails) {
      return mo9(userDetails);
   }

   private int mo9(UserDetails userDetails) {
      return mo10(userDetails);
   }

   private int mo10(UserDetails userDetails) {
      return r.nextInt();
   }

}



