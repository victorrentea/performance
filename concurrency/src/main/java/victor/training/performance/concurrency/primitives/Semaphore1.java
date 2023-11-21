package victor.training.performance.concurrency.primitives;

import victor.training.performance.util.PerformanceUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

import static victor.training.performance.util.PerformanceUtil.*;
import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;

public class Semaphore1 {
	static class BoundedHashSet<T> {
		private final Set<T> set;
		private Semaphore sem;
		
		public BoundedHashSet(int bound) {
			set = Collections.synchronizedSet(new HashSet<T>());
			// TODO create Semaphore with given number of permits
		}
		
		public boolean add(T o) throws InterruptedException {
			// TODO acquire permit from semaphore; if the add failed, release the semaphore back
			 return set.add(o); 
		}
		
		public boolean remove(T o) {
			boolean wasRemoved = set.remove(o);
			// TODO if indeed removed an item, release a virtual permit back to the semaphore
			return wasRemoved;
		}
	}

//	https://www.red-gate.com/blog/software-development/code-kata-7-producer-consumer-problem
	
	public static void main(String[] args) {
		final BoundedHashSet<String> set = new BoundedHashSet<>(3);
		
		new Thread("Inserter") {
			public void run() {
				addElement("a");
				addElement("b");
				addElement("c");
				addElement("d");
				addElement("e");
			}
			private void addElement(String element) {
				try {
					sleepMillis(20);
					log("adding element " + element);
					set.add(element);
					log("added");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		new Thread("Remover") {
			public void run() {
				sleepMillis(200);
				remove("a");
				remove("b");
				remove("c");
				remove("d");
				remove("e");
			}
			private void remove(String element) {
				sleepMillis(20);
				log("removing(" + element + ")");
				set.remove(element);
				log("removed");
			}
		}.start();
		
	}
}
