# How to guide
## Tell Micrometer to collect and expose metrics

- Add micrometer to your pom.xml:
  - spring-boot-starter-actuator
  - io.micronaut.micrometer:micronaut-micrometer-registry-prometheus:3.3.0

- Expose metrics for prometheus, by adding this to  your `application.properties` 
   management.endpoints.web.exposure.include=*
   management.endpoint.prometheus.enabled=true

- Make sure http://localhost:8080/actuator/prometheus returns data


## Configure Prometheus to poll and record the metrics
### Manual
install prometheus and add this to prometheus.yml as a child of `scrape_configs` to tell it to poll the spring boot actuator metrics
```
- job_name: 'spring'
  metrics_path: '/actuator/prometheus'
  static_configs:
  - targets: ['localhost:8081']
```
### Or run the docker-compose.yml
Check that Prometheus is running at localhost:9090 

## Configure Grafana
To query, display and monitor the metrics from Prometheus
- Install grafana locally or run the `docker-compose.yml` in this folder
- Go to grafana at localhost:3000 
- login with admin/admin
- Add a datasource to Prometheus with the url http://host.docker.internal:9090
- Install the dashboard https://grafana.com/grafana/dashboards/6756
- Set as "Instance" variable the value: `host.docker.internal:8080`
- Monitor the "Connection Timeout" count and "Acquire Time"
- Add a panel to display shepard response times. IN the formula, enter
  `sum by ()(rate(shepard_seconds_sum[5m])) / sum by ()(rate(shepard_seconds_count[5m]))`




# TODO 
- zipkin tracing 1->main->2 si 1->2
- un apel http POST din main in altul -> inlocuit cu mq
- +service2 + redis pe el 
- pus errori prin cod.






- clone performance-microservices git from the mail yesterday + import in IntelliJ
- install Docker Desktop
- start this docker/docker-compose.yml (a lot of instances, god help RAM)
- start StartWireMock.java


---- an endpoint is slow even when load tested alone with Gatling in local environmet
- go to glowroot.org and download and unzip
- copy the full path to glowroot.jar
- in IntelliJ open the run configuration of Service2App and add to the VM arguments: -javaagent:<path_to_glowroot_jar>
- start Service2App
- go to localhost:8082/1 -> it 200
- Run EngineJava in IntelliJ and select the simulation "Service2_GetByIdSimulation" (type the number and ENTER +  ENTER (empty desr))
- Click the link to the Gatling report -> response time 100ms mean with normal distribution
- go to localhost:4000
  in the left side click /1
  go to "Thread Profile" tab > click flame graph

- the bottleneck was the findById because there were only 20 onnections by default in the Hikari conenction pool
  spring.datasource.hikari.maximum-pool-size=60
- restart the app (for glowroot to wipe its database)
- run the simulation -> open the report = 80ms mean



=== Java-level deadlocks ===
Deadlock = two parallel flows wait for a resource owned by the other () indefinetly
* Database Table LOCK TABLE(table-lock); SELECT FOR UPDATE (row-lock)
* java-lock: syncronized < really in 2023 the year of the Lord NEVER
! do not pass lambdas to synchronized methods eg. list.removeIf map.computeIfAbsent
* Redis lock

Exercise #1 - main():
- download https://visualvm.github.io/ (as standalone) and start it
- Run Deadlock.java -> the execution hangs
- in visualvm connect to the blocked process, and go to "Threads" tab -> "Deadlock Detected"

Note: Flamegraph will NOT point out the problem, as time waiting for 'synchronized' happens outside of Java (in C code)

Exercise #2 - JFR on a running API:
- Download Java Mission Control https://jdk.java.net/jmc/8/ and open it
- Start Service2
- In JMC, click on Service 2 in VM browser > "Start flight recording"
- Start Search simulation and wait for it to complete
- END the recording in Java Mission Control
  => in JMC, the automatic analysis (first screen) points out Java-Lock Contention were a problem pointing at the instance on which the threads synchronized() on, and showint what threads were blocked and for how long



JFR Inside the JVM : ->.jfr
a) records stack traces -> flame graphs
b) records events -> JVM internals
Mission Control -> load and analyze a .jfr output
Gatling
VisualVM deadlocks



# Troubleshooting
### Troubleshoot starting visualvm on macos if JDK collision
open -a visualvm --args --jdkhome `/usr/libexec/java_home -v 17`

