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
- Complete a CF after a delay
- Combine 2 CF, change what thread executes the BiFunction

## Testing
- complete with result|exception
- mocks returning a CF, then completed.
- detect blocking ??

## Advanced Scenarios
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
- buffer (wait for N, fire all)
- cache: get/put full async
- rate limiting over WebClient adapter