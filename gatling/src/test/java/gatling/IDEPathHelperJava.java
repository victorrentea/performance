package gatling;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IDEPathHelperJava {
  public static Path projectRootDir() {
    try {
      return Paths.get(IDEPathHelperJava.class.getClassLoader().getResource("gatling.conf").toURI())
              .getParent().getParent().getParent();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public static Path mavenTargetDirectory() {
    return projectRootDir().resolve("target");
  }

  public static Path mavenSrcTestDirectory() {
    return projectRootDir()    .resolve("src").resolve("test");
  }


  public static Path mavenSourcesDirectory() {
    return mavenSrcTestDirectory().resolve("java");
  }

  public static Path mavenResourcesDirectory() {
    return mavenSrcTestDirectory().resolve("resources");
  }

  public static Path mavenBinariesDirectory() {
    return mavenTargetDirectory().resolve("test-classes");
  }

  public static Path resultsDirectory() {
    return mavenTargetDirectory().resolve("gatling");
  }

  public static Path recorderConfigFile() {
    return mavenResourcesDirectory().resolve("recorder.conf");
  }
}
