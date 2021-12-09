

- Step1: You have 1 probe: make sure you receive data and request a new sample immediately
  
- Send every sample immediately to plotter (as a size-one list), from the same thread where the `request()` method runs
- Rule: Plotter should receive the samples in their chronological order
- You now have 3 probes
- Rule: Plotter should only be sent one request at a time
  - Task: violate this rule and observe the exception
  - [Pro]: study how the detection of this case is implemented
- Request as many samples possible from the probe 
  - That is: you are NOT allowed to block in the thread running the `receive()`
  - Hint: You'll need your own thread pool. What kind? 
    - `Executors.newCachedThreadPool()` creating unbounded threads or 
    - `Executors.newFixedThreadPool(N)` creating a fixed number of threads; but `N`=?
- For memory efficiency, you should not keep more than 40 samples in memory
  - If too many samples arrive, discard the oldest (make the test pass)
- For network efficiency, Plotter now only accepts pages of 5 samples at a time
    - For this step, please set `PLOTTER_ACCEPTS_ONLY_PAGES` to `true` 
    - Hint: how do you accumulate a full page between threads?
    - IMPORTANT: don't mind about the previous test
- [HARD] Make the test still pass -> manual backpressure implementation
  - Hint: a queue of pages is too coarse-grained
  
