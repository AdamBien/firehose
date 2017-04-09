function apply(input) {
    var metric = JSON.parse(input);
    var value = metric.extraProperties.entity.methodstatistic.lastsampletime;
    var output = {
        suffix: "duraction",
        units: "ms",
        component: "methodstatistic",
        application: "payara",
        value: value
    };
    return JSON.stringify(output);

}

