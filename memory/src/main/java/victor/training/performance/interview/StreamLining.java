package victor.training.performance.interview;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.IdentityHashMap;
import java.util.Map;

public class StreamLining {
  public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
    File file = new File("file-to-upload.txt");
    BigDecimal total;
    try (var stream = Files.lines(file.toPath())) {
//    int total = stream.mapToInt(String::length).sum();
//    long total = stream.mapToLong(String::length).sum();
      total = stream.map(String::length).map(BigDecimal::new)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
    } // sau din QUERY
    System.out.println("caractere : "+ total);

    // o lib uitata de lume (90s) face:
    // tu o folosesti intr-o app server intr-un mediu air-gapped ce ruleaza win xp.
    // @alex tre sa-i dea restart in fiecare Dum noapte ca atlfel crapa. de 5 ani face asta.

    // a) upg lib
    // b) separi lib in proces separat
    // c) reflection access to cele sfinte
    Class<?> aClass = Class.forName("java.lang.ApplicationShutdownHooks");
    var field = aClass.getDeclaredField("hooks");
    field.setAccessible(true); //da-on masa de protectie privata
    IdentityHashMap mapuVechi = new IdentityHashMap<>((Map) field.get(null));
    oMetoda(file); // asta pune +1
    field.set(null, mapuVechi);

    // d) class shadowing < dintr-o lib obscura copiezi codul sursa
    // org.libmasii.ClasaRea.java  + editezi
    // o pui in src/main/java ca Classloaderul sa o ia p'aia prima


  }

  private static void oMetoda(File file) {
    file.deleteOnExit();
    Runtime.getRuntime().addShutdownHook(new Thread(()->{
      System.out.println("sa curat fis temp");
    }));
  }
}
