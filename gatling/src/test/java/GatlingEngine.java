import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GatlingEngine {
  public static void main(String[] args) {
    GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
            .resourcesDirectory(mavenResourcesDirectory().toString())
            .resultsDirectory(resultsDirectory().toString())
            .binariesDirectory(mavenBinariesDirectory().toString());
    Gatling.fromMap(props.build());

  }

  protected static void startClass(Class<?> clazz) {
    GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
            .resourcesDirectory(mavenResourcesDirectory().toString())
            .resultsDirectory(resultsDirectory().toString())
            .binariesDirectory(mavenBinariesDirectory().toString())
            .simulationClass(clazz.getCanonicalName());
    Gatling.fromMap(props.build());
  }


  public static Path projectRootDir() {
    try {
      return Paths.get(GatlingEngine.class.getClassLoader().getResource("gatling.conf").toURI())
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
