package victor.training.performance.swing;

public enum Variant {
   BLOCKING,         // Request1Blocking
   BACKGROUND,       // Request2Background
   CALLBACKS,        // Request3Callbacks
   SUSPEND,          // Request4Coroutine
   CONCURRENT,       // Request5Concurrent
   NOT_CANCELLABLE,  // Request6NotCancellable
   PROGRESS,         // Request6Progress
   CHANNELS          // Request7Channels
}
