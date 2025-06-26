package victor.training.performance.front;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.*;

@Slf4j
public class FrontPollers {
  // out of mem <== unbounded queue
//  static ExecutorService highExecutor = Executors.newFixedThreadPool(5, new NamedThreadFactory("high"));
//  static ExecutorService lowExecutor = Executors.newFixedThreadPool(1, new NamedThreadFactory("low"));

  static ExecutorService highExecutor = new ThreadPoolExecutor(5,5,
      1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(50), new NamedThreadFactory("high"));
  static ExecutorService lowExecutor = new ThreadPoolExecutor(1,2,
      1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(50), new NamedThreadFactory("low"));

  private static final Logger log = LoggerFactory.getLogger("pollere");
  public static void main(String[] args) {
    new ScheduledThreadPoolExecutor(1,
        new NamedThreadFactory("🚽poller"))
        .scheduleWithFixedDelay(() -> {
          try {
            lowExecutor.submit(() -> pollWC("tufis"));
            log.info("Dupa!");
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          }
        }, 0, 1, TimeUnit.MILLISECONDS);

    new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("🚽poller2"))
        .scheduleWithFixedDelay(() -> {
          lowExecutor.submit(() -> pollWC("buncar"));
          log.info("Dupa!");
        }, 0, 1, TimeUnit.MILLISECONDS);

    new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("🚀poller"))
        .scheduleWithFixedDelay(() -> highExecutor.submit(()->pollRocket()), 0, 1, TimeUnit.SECONDS);
  }

  static LocalDateTime lastWCToken = null;
  public static void pollWC(String tip) {
    log.info("🚽 start "+tip);
    var response = PervApi.fetchToalete(lastWCToken, 1000);
    lastWCToken = response.nextToken();
    log.info("Process: " + response.data());
    log.info("🚽 end");
  }

  static LocalDateTime rocketToken = null;
  public static void pollRocket() {
    log.info("🚀 start");
    var response = PervApi.fetchRachete(rocketToken, 1000);
    rocketToken = response.nextToken();
    log.info("Process: " + response.data());
    log.info("🚀 end");
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
