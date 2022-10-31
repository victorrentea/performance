## Basics
- completing with value
- completing with exception

## Combining
- thenApply
- thenRun
- compose (aka flatMap)
- CF0.fork{CF1,CF2}->join{cfResult}
- anyOf and Either

## Exceptions
- catch() {log}
- catch() {wrap exception in new one}
- catch() {default value}
- catch() {call another CF} / retry
- finally {cleanup}

## Thread Pools
- launch on ForkJoinPool.commonPool
- launch on dedicated thread pool
- launch in parallel 2 steps
- Complete a CF after a delay
- Combine 2 CF, change what thread executes the BiFunction

## Testing
- mocks returning a CF: then complete/fail
- mocks delaying response
- integration testing with WireMock

## Real-life Scenarios
- Data enrichment
  - a() and b() ==> AB(a,b)
  - a(), then b1(a) ==> AB(a,b)
  - a(), then b1(a) and c1(a) ==> ABC(a,b,c)
  - a(), then b1(a), then c2(a,b) ==> ABC(a,b,c)
  - a() and b() and c() ==> ABC(a,b,c)
- HTTP-Message Bridge 
  - from a HTTP request send message on request MQ
  - keep the connection open 
  - complete the pending HTTP request
    - with the _corresponding_ reply message received on a reply MQ, or
    - with a message "Order in processing" if no reply message arrives in 3 seconds
- Bidding
  - You receive a HTTP request with a list of strings
  - Within 5 seconds, 
  - Every five seconds emit (log) a signal.
  - If a http req arrives with the next n 5 sec
- timed buffer (wait for N, fire all)
- cache: get/put full async
- rate limiting over WebClient adapter
- profiling