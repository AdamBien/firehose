# firehose

Java EE 7+ metrics gateway for prometheus

The firehose service (16kB Thin WAR) gathers metrics emitted from JSON / text (currently only JSON is supported) HTTP endpoint and converts them into prometheus compliant [metrics](https://prometheus.io/docs/practices/naming/). 

The prometheus metrics are exposed via the: `http://[HOST]:[8080]/firehose/resources/metrics/{metrics-name}` uri in prometheus format.

Metrics from all registered endpoints can be fetched at once via HTTP `http://[HOST]:[8080]/firehose/resources/metrics/`

## Installation

Start `firehose` in the same docker network as the monitored resources and start it with
`docker run -d -p 8080:8080 --name firehose airhacks/firehose` or deploy the WAR to a Java EE 7 container running on Java 8.

A sample configuration is used for testing. Prometheus, a sample-service and firehose 
are started:

```yaml
version: '3.0'
services:
  prometheus:
    build: prometheus-firehose
    ports:
    - "9090:9090"
    links:
    - firehose
  firehose:
    image: airhacks/firehose
    env_file: metrics.env
    ports:
    - "8080:8080"
    links:
    - sample-service
  sample-service:
    image: airhacks/sample-service
    ports:
    - "8282:8080"
```

sample-service is configured in an `metrics.env` file and provided at starttime:

```
# Sample service configuration
firehose.sample-service.uri=http://sample-service:8080/sample-service/resources/metrics
# prometheus does not allow dashes -> name changed to sampleservice
firehose.sample-service.application=sampleservice
```
    
## Configuration management

To gather a remote metric at least the `uri` with the fully qualified value, e.g.: `http://sample-service:8080/sample-service/resources/metrics` is required.

The properties (required by [prometheus](https://prometheus.io/docs/practices/naming/)):

1. application  (e.g. "sampleservice"
2. component  (e.g. "MetricsResource"")
3. units (e.g. "requests")
4. suffix (e.g. "total")

are usually provided by the monitored endpoint as json, but can be overriden with environment entries, like e.g.

`sampleservice.application=sample`

at startup time.

The properties can be also passed as JSON via configuration, e.g.

`curl -i -H"Content-type: application/json" -XPUT -d'{"uri":"http://sample-service:8080/sample-service/resources/metrics","application":"sample", "component":"jobprocessing","units":"jobs","suffix":"total}' http://localhost:8080/firehose/resources/configurations/sample-service`

Also an extractor script can be configured to extract the data from an incompatible data source. The following script uploads a JavaScript file, which extracts the used heap in bytes from
the GlassFish / Payara monitoring endpoint:

`curl -i -XPUT -H"Content-type: text/plain" -H"uri: http://localhost:4848/monitoring/domain/server/jvm/memory/usedheapsize-count.json"  --upload-file usedheapsize.js http://localhost:8080/firehose/resources/configurations/payarausedheap`

The content of the file is:

```
function apply(input) {
    var metric = JSON.parse(input);
    var value = metric.extraProperties.entity["usedheapsize-count"].count;
    var output = {
        suffix: "size",
        units: "bytes",
        component: "jvmusedheapsize",
        application: "firehosest",
        value: value
    };
    return JSON.stringify(output);
}
````
(see firehose-st/src/test/resources)

The following request: `curl http://localhost:8080/firehose/resources/metrics/payarausedheap`

will return a prometheus-consumable metric:

`firehosest_jvmusedheapsize_bytes_size 150629368`



The environment variables can be overriden again by the REST configuration endpoint:

1. create or update a metric

Setup "sample-service" metric to be fetched from `http://sample-service:8080/sample-service/resources/metrics` and exposed
via `http://[HOST]:[8080]/firehose/resources/metrics/sample-service`:

`curl -i -H"Content-type: application/json" -XPUT -d'{"uri":"http://sample-service:8080/sample-service/resources/metrics"}' http://localhost:8080/firehose/resources/configurations/sample-service`

This endpoint return 201 for creation and 204 for update.

2. list all metrics

`curl -i http://localhost:8080/firehose/resources/configurations/`

3. delete a metric

delete a metric with the name "sample-service"

`curl -XDELETE http://localhost:8080/firehose/resources/configurations/sample-service`

## Prometheus endpoint

The preconfigured endpoint are available under the URI: `http://[HOST]:[8080]/firehose/resources/metrics/{metrics-name}`

test it with: e.g. `curl -i http://localhost:8080/firehose/resources/metrics/sample-service`

You can also fetch all metrics via a single URI:
`curl http://localhost:8080/firehose/resources/metrics/`, what does not require any changes in prometheus configuration in case an additional endpoint is added.


## Build

1. `git clone https://github.com/AdamBien/firehose`
2. `cd firehose && mvn clean install && docker build -t airhacks/firehose .`
3. `cd sample-service && mvn clean install && docker build -t airhacks/sample-service .`
4. `docker-compose up -d`


Base image for firehose and sample apps were taken from [docklands](https://github.com/AdamBien/docklands)

See you at [Java EE Performance, Monitoring and Troubleshooting](http://workshops.adam-bien.com/performance.htm) and/or [Java EE Microservices](http://workshops.adam-bien.com/microservices.htm) workshops


