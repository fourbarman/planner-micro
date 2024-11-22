# Planner-micro 
## repo for rebuild monolith to microservices using:
1. Spring
2. Spring Cloud

## Build and run
### Build with Gradle 8.*
### Run 
(don't run planner-entity - it's a project for entity classes)
1. Run planner-config (spring cloud server)
2. Run planner-server (eureka server)
3. Run planner-todo (client)
4. Run planner-user (client)
5. Rung planner-gateway (gateway)