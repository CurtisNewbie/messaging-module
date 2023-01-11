# messaging-module V2.0.7

Module for async messaging using RabbitMQ, it provides convenient services and listener adapters for dispatching and consuming messages.

## Implementation

Relevant beans are populated by the `MessagingModuleAutoConfiguration` auto-configuration bean.

A `RabbitTemplate` with default settings is populated if it's not found. However, you do need to provide the `ConnectionFactory` bean, a convenient factory method is provided to create such bean using properties-based configuration, see `SimpleConnectionFactoryBeanFactory`.

`MessageConverter` is automatically configured to use `Jackson2JsonMessageConverter` if no `MessageConverter` bean is found. If you prefer to use the default `SimpleMessageConverter` you will need to populate one by a `@Bean` annotated method.

To send messages, simply inject and use `MessagingService`. Queues, Exchanges, and Listeners are created on your own, through any ways you like. However, there is a listener adapter `AbstractJsonDeserializedListenerAdapter` that conveniently converts json string to generic type.

## Configuration

Data Type | Property Name | Description | Default Value
----------|---------------|-------------|---------------
boolean | messaging-module.tracing.enabled | enable log tracing for messages, this is achieved by using message `headers`; traceId is put into message `headers` before dispatching, then the traceId is extracted by `AbstractTracingListener` and put into MDC. This may not work properly as expected, because the listener container use its own thread pool, without extra effort, MDC context is not copied to other thread and it's hard to clean it up properly. | false

## Modules and Dependencies

This project depends on the following modules that you must manually install (using `mvn clean install`).

- [curtisnewbie-bom](https://github.com/CurtisNewbie/curtisnewbie-bom)
- [common-module v2.1.3](https://github.com/CurtisNewbie/common-module/tree/v2.1.3)
- [redis-util-module v2.0.3](https://github.com/CurtisNewbie/redis-util-module/tree/v2.0.3)
