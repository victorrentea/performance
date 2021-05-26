## Find the Leak

- Run the program with max heap = 200 MB and instruct java to heap dump on out of memory
- Notice the decrease in speed of processing the items as it gets close to OOME
- Load the generated heapdump in visualvm
- Trace the Leak
- Fix the code