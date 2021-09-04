# messaging-module

Module for async messaging using RabbitMQ, it provides convenient services and listener adapters for dispatching and consuming messages.

## Configuration

Data Type | Property Name | Description | Default Value
----------|---------------|-------------|---------------
boolean | messaging-module.tracing.enabled | enable log tracing for messages, this is achieved by using message `headers`; traceId is put into message `headers` before dispatching, then the traceId is extracted by `AbstractTracingListener` and put into MDC. This may not work properly as expected, because the listener container use its own thread pool, without extra effort, MDC context is not copied to other thread and it's hard to clean it up properly. | false

## Modules and Dependencies

This project depends on the following modules that you must manually install (using `mvn clean install`).

- curtisnewbie-bom
    - description: BOM file for dependency management
    - url: https://github.com/CurtisNewbie/curtisnewbie-bom
    - branch: main
    - under `/microservice` folder

- log-tracing-module
    - description: for log tracing between web endpoints and service layers
    - url: https://github.com/CurtisNewbie/log-tracing-module
    - branch: main

- common-module
    - description: for common utility classes 
    - url: https://github.com/CurtisNewbie/common-module
    - branch: main