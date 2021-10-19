package victor.training.performance.interview;

//import static java.util.Arrays.asList;
import static victor.training.performance.util.PerformanceUtil.log;

public class ThreadVsProcess {

    public static void main(String[] args) {
        Runnable r = new Runnable() {
			@Override
			public void run() {
				log("Halo2");
//				run();
			}
		};
		
		Thread t = new Thread(r);
		t.start();
		log("Done");
		// Un process are o zona de memorie privata in care gazduieste mai multe treaduri
		
//		Runtime.getRuntime().exe
//		ProcessBuilder pb = new ProcessBuilder(asList("a.exe", "b","c"));
//		Process process = pb.start();
//		pb.
//		process.
//		
    }
}
