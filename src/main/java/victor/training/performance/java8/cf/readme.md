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
- timeout > ex | value
- Message Bridge (fire on request MQ, wait on a reply MQ to complete the pending httpRequest
- buffer (wait for N, fire all)
- cache: get/put full async
- rate limiting over WebClient adapter