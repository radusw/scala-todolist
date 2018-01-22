# scala-todolist
Functional Scala Todo-List using doobie, flyway, circe, akka-http, monix

## Installation

Build a docker image:
```
$sbt docker:publishLocal
```

Run a container:
```
$docker run -d -e PG_HOST='docker.for.mac.localhost' \
 -e PG_PORT='5432' \
 -e PG_USER='dev' \
 -e PG_DATABASE_NAME='world' \
 -p 9000:9000 --restart unless-stopped --name todo radusw/todo-list:latest
```

Use:
```
$docker logs todo --follow

$curl http://localhost:9000/api/todos
```

## Running locally
```
sbt "run conf/dev.conf"
```