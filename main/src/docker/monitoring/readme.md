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
