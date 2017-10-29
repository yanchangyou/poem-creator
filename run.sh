#1/bin/bash

git pull
./mvnw spring-boot:stop 
./mvnw spring-boot:run &
