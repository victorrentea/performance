//WIP

//package victor.training.performance.spring;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//
//import static victor.training.performance.util.PerformanceUtil.sleepMillis;
//
//@Slf4j
//@RestController
//@RequestMapping("profile/javalock2")
//public class Profile9_JavaLock2 {
//  // Note: language of a user is expected NOT to change; max no of users < 1000
//  private static final Map<String, Locale> userLanguage = Collections.synchronizedMap(new HashMap<>());
//
//  @GetMapping
//  public String sayHi() {
//    String username = "jdoe"; // in reality, extracted from Access Token
//
//    // Locale userLocale = fetchLocaleFromUserinfo(username); "inefficient"
//    Locale userLocale = userLanguage.computeIfAbsent(username, u -> fetchLocaleFromUserinfo(username));
//    return String.format("Hi %s(%s)!", username, userLocale.getISO3Language());
//  }
//
//  private Locale fetchLocaleFromUserinfo(String username) {
//    log.info("GET from OpenID Connect /userinfo for user " + username);
//    sleepMillis(50);
//    return Locale.US;
//  }
//}
