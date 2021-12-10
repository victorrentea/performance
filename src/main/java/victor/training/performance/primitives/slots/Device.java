package victor.training.performance.primitives.slots;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import victor.training.performance.util.PerformanceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class Device {
   private final List<Slot> slots = new ArrayList<>();
   private boolean open = true;

   public void addSlot(Slot slot) {
      slots.add(slot);
      slot.setParentDevice(this);
   }

   public void close() {
      synchronized (this) {
         if (open == false) {
            throw new IllegalStateException("Already closed");
         }
         open = false;
      }
      for (Slot slot : slots) {
         slot.close();
      }
   }

   public boolean isOpen() {
      return open;
   }
}

class Slot {
   private static final Logger log = LoggerFactory.getLogger(Slot.class);
   private Device parentDevice;
   private boolean slotIsOpen = true;

   public void setParentDevice(Device parentDevice) {
      this.parentDevice = parentDevice;
   }

   public synchronized void sendData(String data) {
      if (!parentDevice.isOpen()) throw new RuntimeException("Parent device closed");
      if (!slotIsOpen) throw new IllegalStateException("Slot closed!");

      log.debug("Sending to {}" + this + " data: " + data);
      PerformanceUtil.sleepq(1000);
      log.debug("Sent");
   }

   public synchronized void close() {
      if (slotIsOpen == false) {
         throw new IllegalStateException("Already closed");
      }
      slotIsOpen = false;
   }
}

class DevicePlay {
   private static final Logger log = LoggerFactory.getLogger(DevicePlay.class);

   public static void main(String[] args) {
      Device device = new Device();
      List<Slot> slots = IntStream.range(0, 100).mapToObj(i -> new Slot()).collect(toList());
      slots.forEach(device::addSlot);

      ExecutorService pool = Executors.newCachedThreadPool();
      for (int i = 0; i < 20; i++) {
         pool.submit(() -> DevicePlay.useRandomSlot(slots));
      }
      PerformanceUtil.sleepq(2000);
      log.debug("attempt to close parent");
      device.close();
      log.debug("Closed");


   }

   public static void useRandomSlot(List<Slot> slots) {
      while (true) {
         int i = new Random().nextInt(slots.size());
         Slot slot = slots.get(i);
         slot.sendData("data");
      }

   }
}