package gatling;

import io.gatling.recorder.config.RecorderPropertiesBuilder;
import scala.Some;

public class GatlingRecorder {

  public static void main(String[] args) {

    var props = new RecorderPropertiesBuilder()
            .simulationsFolder(IDEPathHelperJava.mavenSourcesDirectory().toString())
            .resourcesFolder(IDEPathHelperJava.mavenResourcesDirectory().toString())
            .simulationPackage("performance");

    io.gatling.recorder.GatlingRecorder.fromMap(props.build(), Some.apply(IDEPathHelperJava.recorderConfigFile()));

  }
}
