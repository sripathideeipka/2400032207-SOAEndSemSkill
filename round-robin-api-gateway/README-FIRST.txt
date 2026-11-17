ROUND-ROBIN LOAD BALANCING AT API GATEWAY
========================================

Requirements
------------
- JDK 21
- Spring Tool Suite (STS) with Maven support
- Internet connection for the first Maven dependency download

PROJECTS
--------
1. eureka-server  : Eureka registry at http://localhost:8761
2. driver-service : Driver microservice; server.port=0 means dynamic port
3. api-gateway    : Gateway at http://localhost:8080

IMPORT INTO STS
---------------
1. Extract this ZIP.
2. In STS select File > Import > Maven > Existing Maven Projects.
3. Browse to the extracted round-robin-api-gateway folder.
4. Select the root pom.xml and click Finish.
5. Wait for Maven to download dependencies and finish updating the projects.
   The first import may take a few minutes.

RUN IN THIS EXACT ORDER
-----------------------
A. Eureka Server
   - Open eureka-server > src/main/java > EurekaServerApplication.java
   - Right-click > Run As > Spring Boot App.
   - Confirm it starts on port 8761.

B. Driver Service - THREE INSTANCES
   - Open driver-service > src/main/java > DriverServiceApplication.java.
   - Right-click > Run As > Run Configurations...
   - Select Spring Boot App, create a launch configuration named Driver-1.
   - Project: driver-service
   - Main type: com.example.driverservice.DriverServiceApplication
   - Arguments tab > Program arguments: --server.port=0
   - Apply > Run.
   - Repeat by creating Driver-2 and Driver-3 with the same project,
     main type, and program argument --server.port=0.
   - Keep all three running. Each should print a different Tomcat port.
   - Wait about 30-60 seconds for Eureka registration.

C. Confirm Eureka registration
   - Open http://localhost:8761
   - Under "Instances currently registered with Eureka", confirm
     DRIVER-SERVICE has three registered instances.

D. API Gateway
   - Open api-gateway > src/main/java > ApiGatewayApplication.java.
   - Right-click > Run As > Spring Boot App.
   - Confirm it starts on port 8080.

TEST ROUND-ROBIN
----------------
Open Windows PowerShell and run:

1..9 | ForEach-Object {
    Invoke-RestMethod "http://localhost:8080/drivers/101"
}

Expected: responses show three different dynamic ports, repeating in
round-robin order (for example P1, P2, P3, P1, P2, P3...). The exact
starting instance and port numbers can differ.

You can also test one request in a browser:
http://localhost:8080/drivers/101

WHAT TO SHOW YOUR FACULTY
-------------------------
1. Show Eureka dashboard with three DRIVER-SERVICE instances.
2. Show the three Driver Service console logs and their different ports.
3. Show api-gateway/src/main/resources/application.properties:
       spring.cloud.gateway.routes[0].uri=lb://DRIVER-SERVICE
       spring.cloud.gateway.routes[0].predicates[0]=Path=/drivers/**
4. Show the PowerShell command sending multiple GET requests.
5. Show PowerShell responses with the three ports rotating.
6. Show Driver Service console logs proving requests reached each instance.

VIVA
----
1. How does Round-Robin distribute requests?
   It selects available service instances one by one in circular order.
   After the last instance, it starts again from the first.

2. What happens if one instance fails?
   Once service discovery/load balancing recognizes the instance as
   unavailable, new requests are sent to the remaining healthy instances.
   Detection and registry updates may take a short time.

TROUBLESHOOTING
---------------
- If Maven dependencies are unresolved: right-click root project >
  Maven > Update Project, check Force Update, then Finish.
- If port 8761 or 8080 is occupied, stop the other application using it.
- If only one Driver instance appears, make sure all three launch
  configurations are running and each has --server.port=0.
- Wait 30-60 seconds after starting Driver instances for Eureka registration.
- If Gateway returns 503, check Eureka registration and confirm that
  spring-cloud-starter-loadbalancer is present in api-gateway/pom.xml.
- Do not run the same Driver launch configuration three times; create
  three separate launch configurations.

NOTE
----
The project source and configuration are prepared for Java 21 with
Spring Boot 3.3.5 and Spring Cloud 2023.0.3. Maven must download the
dependencies on first import. A successful build/run still depends on
your local JDK, Maven setup, and network access.
