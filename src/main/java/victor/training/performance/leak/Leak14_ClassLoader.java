package victor.training.performance.leak;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class Leak14_ClassLoader {
  private static Class<?> classLoadPlugin(File classFile) throws MalformedURLException, ClassNotFoundException {
    URL currentFolderUrl = classFile.getParentFile().toURI().toURL();
    // separate classloader prevents classname conflicts
    ClassLoader classLoader = URLClassLoader.newInstance(new URL[]{currentFolderUrl});
    return Class.forName("Plugin", true, classLoader);
  }

  @GetMapping("leak14")
  public String uploadNewPluginVersion() throws Exception {
    currentVersion++;
    File classFile = compilePlugin(currentVersion);
    Class<?> clazz = classLoadPlugin(classFile);

    // Case1: plugin.jar/webapp.war starts a thread
//    clazz.getMethod("start").invoke(null);
    // TODO FIX: stop the old plugin
//    if (lastPluginClass != null) lastPluginClass.getMethod("stop").invoke(null);

    // Case2: an instance of a class in Plugin jar is added to a list
    plugins.add(clazz.newInstance());

    // TODO FIX: dereference the old plugin
//    if (lastPluginClass != null) plugins.clear();

    lastPluginClass = clazz;
    return "Uploaded new plugin version " + currentVersion + " from: " + classFile;
  }

  int currentVersion = 0;

  static Class<?> lastPluginClass; // to stop or remove on classload new

  static List<Object> plugins = new ArrayList<>();

  public File compilePlugin(int version) throws IOException {
    // We have to compile a .class on the fly so that it's NOT in target/classes,
    // otherwise the call to URLClassCoader.loadClass("Plugin")
    // would be served by the top level app classloader - the class never loaded by the new URLClassLoader.

    //language=java
    File file = Files.writeString(Path.of("Plugin.java"), """
            public class Plugin {
                static int[] data = new int[5*1024*1024]; // 10MB
                static java.util.Timer timer = new java.util.Timer();
                public static void start() {
                    System.out.println("Installing plugin v%s loaded by: " + Plugin.class.getClassLoader());
                    timer.schedule(new java.util.TimerTask() {
                      public void run() {
                        tick();
                      }
                    },100, 1000);
                }
                static void tick() {
                  // some periodic s*it
                  System.out.println("Tick plugin v%s");
                }
                public static void stop() {
                  timer.cancel();
                }
                public static void register() {
                  //victor.training.performance.leak.Leak14_ClassLoader.registerPlugin(this);
                  // TODO to compile, must have my SPI in its javac classpath
                }
            }
        """.formatted(version, version)).toFile();
    return compile(file);
  }

  /** @return .class compiled file */
  private File compile(File file) {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    int result = compiler.run(null, null, null, file.getPath());
    if (result != 0) throw new RuntimeException("Compilation failed");
    return new File(file.getAbsolutePath().replace(".java", ".class"));
  }


}