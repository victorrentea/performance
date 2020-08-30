package victor.training.performance.primitives;

import static victor.training.performance.ConcurrencyUtil.log;
import static victor.training.performance.ConcurrencyUtil.sleepq;

import java.util.ArrayList;
import java.util.List;

class Holder<T> {
	public T value;
}

public class MonitorWaitNotifyBasic {
	static Holder<String> masaTaskuri = new Holder<>();

	public static class Sef {
		public void announceComision(String results) {
				masaTaskuri.value = results;
			
				// TODO masaTaskuri.notifyAll();
			log("Announced task");
		}
	}
	
	public static class AngajatModel extends Thread {
		public void waitForTask() {
			log("Waiting for task..");
			
			 sleepq(1); // Force thread shift
			// TODO masaTaskuri.wait();
			
			log("Got task to do: " + masaTaskuri.value);
		}
		
		public void run() {
			waitForTask();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		long t0 = System.currentTimeMillis();
		
		List<AngajatModel> angajati = new ArrayList<>();
		for (int i = 0 ; i< 100; i++) {
			AngajatModel s = new AngajatModel();
			s.start();
			angajati.add(s);
		}
		
		Sef sef = new Sef();
		sef.announceComision("Cafea");
		sef.announceComision("Tzigari");
		sef.announceComision("Palinca");
		
		for (AngajatModel s : angajati) {
			s.join();
		}
		
		
		// TODO measure difference for 10000 tasks after applying double checked locking pattern
		log("Execution took: " +(System.currentTimeMillis() - t0));
	}
}
