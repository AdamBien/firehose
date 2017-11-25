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