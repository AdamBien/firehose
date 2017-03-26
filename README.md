# firehose
Java EE 7+ metrics gateway for prometheus

The firehose service gathers metrics emitted from JSON / text HTTP endpoint and converts them into 
prometheus compliant [metrics](https://prometheus.io/docs/practices/naming/). 

The prometheus metrics are exposed via the: `http://[HOST]:[8080]/firehose/resources/metrics/{metrics-name}` endpoint.

## Installation

Start `firehose` in the same docker network as the monitored resources and start it with
`docker run -d -p 8080:8080 --name firehose airhacks/firehose` or deploy the WAR to a Java EE 7 container running on Java 8.

## Configuration management

1. create or update a metric

Setup "sample-service" metric to be fetched from `http://sample-service:8080/sample-service/resources/metrics` and exposed
via `http://[HOST]:[8080]/firehose/resources/metrics/sample-service`:

`curl -i -H"Content-type: application/json" -XPUT -d'{"uri":"http://sample-service:8080/sample-service/resources/metrics"}' http://localhost:8080/firehose/resources/configurations/sample-service`

2. list all metrics

`curl -i http://localhost:8080/firehose/resources/configurations/`

3. delete a metric

delete a metric with the name "sample-service"

`curl -XDELETE http://localhost:8080/firehose/resources/configurations/sample-service`

## Build

1. `git clone https://github.com/AdamBien/firehose`
2. `cd firehose && mvn clean install && docker build -t airhacks/firehose .`
3. `cd sample-service && mvn clean install && docker build -t airhacks/sample-service .`
4. `docker-compose up -d`


