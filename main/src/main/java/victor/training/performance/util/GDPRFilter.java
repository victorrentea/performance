package victor.training.performance.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

@Component
@Aspect
public class GDPRFilter {

  @Retention(RetentionPolicy.RUNTIME)
  public @interface VisibleFor{
    String value();
  }

  @Around("@within(org.springframework.web.bind.annotation.RestController))") // method of @Facade classes
  public Object clearNonVisibleFields(ProceedingJoinPoint pjp) throws Throwable {
    String currentUser = "uu"; //pretend
    //  🛑 Invisible network call per each request ~> awareness++, cache. Fixes = ? (1) bring this data inside AccessToken/request headers👌
    // At least make sure the DTO returned DOES containt @VisibleFor before firing the request/
    String userJurisdiction = new RestTemplate()
            .getForObject("http://localhost:9999/fast20ms", String.class);
    Object result = pjp.proceed();
    if (result == null) {
      return result;
    }
    if (!result.getClass().getPackageName().startsWith("victor")) {
      return result;
    }
    for (Field field : result.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      VisibleFor annot = field.getAnnotation(VisibleFor.class);
      if (annot != null) {
        if (!annot.value().equals(userJurisdiction)) {
          field.set(result, null);
        }
      }
    }
//    System.out.println("Filtered columns");
    return result;
  }
}
