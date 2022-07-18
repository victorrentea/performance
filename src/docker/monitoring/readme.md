
1) make sure http://localhost:8081/actuator/prometheus returns data
- add to application.properties:
management.endpoints.web.exposure.include=*
management.endpoint.prometheus.enabled=true
  
- add to pom.xml:
spring-boot-starter-actuator
io.micronaut.micrometer:micronaut-micrometer-registry-prometheus:3.3.0

2) install prometheus
add this to prometheus.yml as a child of `scrape_configs`
```
- job_name: 'spring'
  metrics_path: '/actuator/prometheus'
  static_configs:
  - targets: ['localhost:8081']
```

3) start-grafana.bat
login with admin/admin

4) in grafana http://localhost:3000/
   (user/pass  admin/admin)
   Add a datasource to prometheus http://host.docker.internal:9090
   
   Then add a Panel + a query like "http_server_requests_seconds_max"
   rate(http_server_requests_seconds_count[1m])
   cache_size   -- after adding cache management