package victor.training.performance.spring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victor.training.performance.util.BigObject20MB;

@RestController
@RequestMapping("leak1")
public class Leak1_ThreadLocal {
   public static ThreadLocal<BigObject20MB> threadLocalMetadata = new ThreadLocal<>();

   @GetMapping
   public String test() {
      BigObject20MB bigObject = new BigObject20MB();
      bigObject.someString = "john.doe"; // username

      // legi de threadul curent date grele si NU le scoti de pe thread.
      // threadul Tomcat pleaca inapo in poolul de 200 cu 20mb de picior
      // pe local Tocmcat nu are nevoie de mai mult de 10 (=core size) 10x20 =200 MB
      // sub stress (load) tomcat mareste pool la 200 x 20 = 4GB = OOME

      // DACA VREODATA TE MANANCA (n-ar trebuie sa te manance)
      // sa te joci NU UITA sa faci try {} finally{remove()}
      // Sr: dc te prind cu ThreadLocal te bag la FixIt. In loc fa :
      // @Scope("request"), ia din Toke informatii de user
      //   SecurityContextHolder.getAuthentication().getPrincipal(): Object
      threadLocalMetadata.set(bigObject);
      try {
         businessMethod1();
      } finally {
         threadLocalMetadata.remove(); //
      }

      return "Magic can do harm.";
   }

   private void businessMethod1() { // no username in the signature
      businessMethod2();
   }

   private void businessMethod2() {
      BigObject20MB bigObject = threadLocalMetadata.get();
      String currentUsernameOnThisThread = bigObject.someString;
      System.out.println("Business logic using " + currentUsernameOnThisThread);
      // TODO what if throw new RuntimeException(); ?
   }
}

/**
 * KEY POINTS:
 * !! Avoid creating ThreadLocal variables yourself - use the safe ones managed by framework:
 * - SecurityContextHolder, @Scope("request" or "session")
 * - @Transactional, Persistence Context, JDBC Connection
 * - Logback MDC
 * If you insist to use ThreadLocal, after the first .set(), always do try { } finally { .remove(); }
 */
