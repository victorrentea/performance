		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter</artifactId>
		</dependency>


in application.properties:
management.endpoints.web.exposure.include= *
in YAML: '*'


-Dspring.config.location=C:\workspace\spring\from-start\src\main\resources\application.yml