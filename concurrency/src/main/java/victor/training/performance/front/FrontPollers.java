package victor.training.performance.front;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FrontPollers {

  //  static Executor highPrioExecutor = new ThreadPoolExecutor(
//      5,5,
//      1, TimeUnit.MINUTES,
//      ??
//  )
  static ScheduledThreadPoolExecutor wcScheduler = new ScheduledThreadPoolExecutor(1,
      new NamedThreadFactory("🚽poller"));
  static ScheduledThreadPoolExecutor rocketScheduler = new ScheduledThreadPoolExecutor(1,
      new NamedThreadFactory("🚀poller"));

  public static void main(String[] args) {
    wcScheduler.scheduleWithFixedDelay(() -> {
      pollWC();
    }, 0, 1, TimeUnit.SECONDS);
    wcScheduler.scheduleWithFixedDelay(() -> {
      pollRocket();
    }, 0, 1, TimeUnit.SECONDS);
  }

  public static void pollWC() {
    log.info("Poll 🚽 start");

    log.info("Poll 🚽 end");
  }

  public static void pollRocket() {
    log.info("Poll 🚀 start");

    log.info("Poll 🚀 end");
  }

  private record NamedThreadFactory(String name) implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
      var t = new Thread(r);
      t.setName(name);
      return t;
    }
  }

  // ScheduledThreadPoolExecutor e = new ScheduledThreadPoolExecutor(1);
  //     e.scheduleWithFixedDelay(()->{
  //         var future = redExecutor.submit(()-> {
  //           var racheteList = scarbosssu.getRachete(token, maxDelta:1000 !important);
  //           updates = sqllite.merge(racheteList);
  //           socket.publish(updates);
  //
  //           // daca a venit o pagina plina, mai cer imediat
  //           if (racheteList.size()==1000) redExecutor.submit(tot eu); // sau return true=>resubmit acelasi task din nou
  //         });
  //         future.get(); // pe futureul intors, ca sa nu creada schedulerul asta ca e gata tasku
  //
  //       },0, 5, TimeUnit.SECONDS);
  //
  //// redExecutor(size=5), greenExecutor(size=1)


}
