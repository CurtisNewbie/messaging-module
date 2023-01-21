# messaging-module V2.0.8

Module for async messaging using RabbitMQ, it provides convenient services and listener adapters for dispatching and consuming messages.

## Implementation

Relevant beans are populated by the `MessagingModuleAutoConfiguration` auto-configuration bean.

A `RabbitTemplate` with default settings is populated if it's not found. However, you do need to provide the `ConnectionFactory` bean, a convenient factory method is provided to create such bean using properties-based configuration, see `SimpleConnectionFactoryBeanFactory`.

`MessageConverter` is automatically configured to use `Jackson2JsonMessageConverter` if no `MessageConverter` bean is found. If you prefer to use the default `SimpleMessageConverter` you will need to populate one by a `@Bean` annotated method.

To send messages, simply inject and use `MessagingService` (or `MqHelper`, which is a static wrapper of `MessagingService`). Queues, Exchanges, and Listeners are created on your own, through any ways you like. 

`@MsgListener` can be used to register listeners as follows.

```java
@EnableMsgListener
@SpringBootApplication
public class MyApp {

}

@Component
public class MyListener {

    @MsgListener(queue = "myqueue", exchange = "myexchange", routingKey = "#")
    public void onMessage(MyMessage msg) {
        // ...
    }
}
```

## Configuration

Data Type | Property Name | Description | Default Value
----------|---------------|-------------|---------------
boolean | messaging.concurrent-declaration | Whether the declaration is concurrent (executed in CompletableFuture) | false
int | messaging.listener.retry.max-attempt | Max attempt for `RetryTemplate` used by `@MsgListener` | 8
int | messaging.listener.retry.backoff.initial-interval | Initial Interval for `ExponentialBackOffPolicy` used by `@MsgListener` | 500
int | messaging.listener.retry.backoff.multiplier | Multiplier for `ExponentialBackOffPolicy` used by `@MsgListener` | 2
int | messaging.listener.retry.backoff.max-interval | Max Interval for `ExponentialBackOffPolicy` used by `@MsgListener` | 3000

## Modules and Dependencies

This project depends on the following modules that you must manually install (using `mvn clean install`).

- [curtisnewbie-bom](https://github.com/CurtisNewbie/curtisnewbie-bom)
- [common-module v2.1.3](https://github.com/CurtisNewbie/common-module/tree/v2.1.3)
- [redis-util-module v2.0.3](https://github.com/CurtisNewbie/redis-util-module/tree/v2.0.3)
