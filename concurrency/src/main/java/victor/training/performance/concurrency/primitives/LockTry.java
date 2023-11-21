package victor.training.performance.concurrency.primitives;

import victor.training.performance.util.PerformanceUtil;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static victor.training.performance.util.PerformanceUtil.*;
import static victor.training.performance.util.PerformanceUtil.log;
import static victor.training.performance.util.PerformanceUtil.sleepMillis;
import static victor.training.performance.util.PerformanceUtil.sleepSomeTime;

public class LockTry {
	static class Friend extends Thread {
		private final Lock lock = new ReentrantLock();
		private Friend friend;

		public Friend(String name) {
			super(name);
		}

		public void setFriend(Friend friend) {
			this.friend = friend;
		}

		public boolean tryGetBothLocks(Friend bower) {
			
			// TODO the following dead locks T1: A,B; T2: B,A. Fix it.
			lock.lock();
			sleepMillis(1);
			bower.lock.lock();
			return true;
			
		}

		public void bowTo(Friend friend) {
			if (tryGetBothLocks(friend)) {
				try {
					log("I started bowing");
					sleepSomeTime(10, 30);
					log("I bowed");
				} finally {
					lock.unlock();
					friend.lock.unlock();
				}
			} else {
				log("I wanted to bow, but I saw " + friend.getName() + " already started bowing");
			}
		}

		public void run() {
			for (int i=0; i < 20; i++) {
				sleepSomeTime(200, 300);
				bowTo(friend);
			}
		}
	}


	public static void main(String[] args) {
		sleepMillis(1);
		Friend alphonse = new Friend("Alphonse");
		Friend gaston = new Friend("Gaston");
		
		alphonse.setFriend(gaston);
		gaston.setFriend(alphonse);
		
		alphonse.start();
		gaston.start();
	}
}
