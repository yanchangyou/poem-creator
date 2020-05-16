#1/bin/bash

git pull

curl -X POST http://127.0.0.1:59090/manage798/shutdown

./mvnw spring-boot:run >> log.log &
