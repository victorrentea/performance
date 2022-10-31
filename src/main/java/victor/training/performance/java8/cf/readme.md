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

## Enrich Data
(sequential, or parallel ||)
- a() || b() ==> AB(a,b)
- a(), then b1(a) ==> AB(a,b)
- a(), then b1(a) || c1(a) ==> ABC(a,b,c)
- a(), then b1(a), then c2(a,b) ==> ABC(a,b,c)
- a() || b() || c() ==> ABC(a,b,c)

## Complex Real-Life Scenarios
- HTTP-Message Bridge 
  - from a HTTP request send message on request MQ
  - keep the connection open 
  - complete the pending HTTP request
    - with the _corresponding_ reply message received on a reply MQ (hint: CorrelationID pattern), or
    - with a message "Order in processing" if no reply message arrives in 3 seconds
- Bidding
  - You receive a HTTP request with a List<String> clientIDs
  - Within 5 seconds, 
  - Every five seconds emit (log) a signal.
  - If a http req arrives with the next n 5 sec
- merge data: get/put async
- rate limiting over WebClient adapter
- profiling