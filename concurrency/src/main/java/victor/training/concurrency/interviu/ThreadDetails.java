package victor.training.concurrency.interviu;

//import static java.util.Arrays.asList;
import static victor.training.concurrency.ConcurrencyUtil.log;

import victor.training.concurrency.ConcurrencyUtil;

public class ThreadDetails {

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
