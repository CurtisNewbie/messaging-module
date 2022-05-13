package com.curtisnewbie.module.messaging.config;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.*;

import java.util.*;

import static com.curtisnewbie.module.messaging.config.DefaultConnectionFactoryProvider.ConnectionFactoryProp.*;
import static java.lang.String.*;

/**
 * <p>
 * Simple bean factory for {@link org.springframework.amqp.rabbit.connection.ConnectionFactory}
 * </p>
 * <p>
 * This class is used to facilitate programmatic configuration of a ConnectionFactory bean
 * </p>
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Configuration
public class DefaultConnectionFactoryProvider {

    /**
     * <p>
     * Create a basic {@link CachingConnectionFactory} based on a set of property values, the publisher confirm is
     * enabled and set to {@link CachingConnectionFactory.ConfirmType#SIMPLE}
     * </p>
     * <ul>
     *   <li>spring.rabbitmq.virtualHost</li>
     *   <li>spring.rabbitmq.host</li>
     *   <li>spring.rabbitmq.password</li>
     *   <li>spring.rabbitmq.username</li>
     *   <li>spring.rabbitmq.port</li>
     * </ul>
     *
     * @param environment environment used to get the required property
     * @return a new CachingConnectionFactory that can be further configured
     */
    @Bean
    @ConditionalOnMissingBean(ConnectionFactory.class)
    public CachingConnectionFactory createByProperties(Environment environment) {
        ConnProp connProp = new ConnProp(environment);
        final CachingConnectionFactory cachingConnectionFactory = ConnProp.buildCachingConnectFactoryFromProp(connProp);
        log.info("Populating default CachingConnectionFactory for RabbitMQ, properties: {}", connProp);
        return cachingConnectionFactory;
    }

    @Getter
    @ToString
    public static class ConnProp {

        private final String virtualHost;
        private final String host;
        private final String password;
        private final String username;
        private final int port;
        private final CachingConnectionFactory.ConfirmType publisherConfirmType;

        /**
         * Create ConnProp by reading the environment
         */
        public ConnProp(Environment environment) {
            virtualHost = VIRTUAL_HOST.getRequired(environment);
            host = HOST.getRequired(environment);
            password = PASSWORD.getRequired(environment);
            username = USERNAME.getRequired(environment);
            port = Integer.parseInt(PORT.getRequired(environment));
            CachingConnectionFactory.ConfirmType confirmType = parseConfirmType(CONFIRM_TYPE.get(environment));
            if (confirmType == null) {
                confirmType = CachingConnectionFactory.ConfirmType.SIMPLE;
                log.info("Property '{}' is not specified, using default ConfirmType: {}", CONFIRM_TYPE.getKey(), confirmType);
            }
            publisherConfirmType = confirmType;
        }

        /**
         * Build {@link CachingConnectionFactory} based on provided properties
         */
        public static CachingConnectionFactory buildCachingConnectFactoryFromProp(ConnProp prop) {
            CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
            connectionFactory.setVirtualHost(prop.getVirtualHost());
            connectionFactory.setHost(prop.getHost());
            connectionFactory.setPassword(prop.getPassword());
            connectionFactory.setUsername(prop.getUsername());
            connectionFactory.setPort(prop.getPort());
            connectionFactory.setPublisherConfirmType(prop.getPublisherConfirmType());
            return connectionFactory;
        }

        /**
         * Parse {@link CachingConnectionFactory.ConfirmType}
         *
         * @param c ConfirmType's name (case is ignored)
         * @return ConfirmType, or null if {@code c == null}
         */
        public static CachingConnectionFactory.ConfirmType parseConfirmType(String c) {
            if (c == null)
                return null;

            final CachingConnectionFactory.ConfirmType[] values = CachingConnectionFactory.ConfirmType.values();
            for (CachingConnectionFactory.ConfirmType value : values) {
                if (value.name().equalsIgnoreCase(c))
                    return value;
            }
            throw new IllegalArgumentException(format("Unable to parse CachingConnectionFactory.ConfirmType for value '%s'", c));
        }
    }

    @Getter
    public enum ConnectionFactoryProp {

        VIRTUAL_HOST("messaging.rabbitmq.virtualHost", "spring.rabbitmq.virtualHost", "Virtual Host for RabbitMQ"),

        HOST("messaging.rabbitmq.host", "spring.rabbitmq.host", "Host for RabbitMQ"),

        PASSWORD("messaging.rabbitmq.password", "spring.rabbitmq.password", "Password for RabbitMQ"),

        USERNAME("messaging.rabbitmq.username", "spring.rabbitmq.username", "Username for RabbitMQ"),

        PORT("messaging.rabbitmq.port", "spring.rabbitmq.port", "Port for RabbitMQ"),

        CONFIRM_TYPE("messaging.rabbitmq.publisherConfirmType", null, format("Publisher Confirm Type (%s) for RabbitMQ", Arrays.asList(CachingConnectionFactory.ConfirmType.values())));

        private final String key;
        @Nullable
        private final String compatibleKey; // for backward compatibility
        private final String desc;

        ConnectionFactoryProp(String key, String compatibleKey, String description) {
            this.key = key;
            this.compatibleKey = compatibleKey;
            this.desc = description;
        }

        @Nullable
        public String get(Environment environment) {
            String property = environment.getProperty(key);
            if (property != null || compatibleKey == null)
                return property;
            return environment.getProperty(compatibleKey);
        }

        public String getRequired(Environment environment) {
            final String prop = get(environment);
            if (prop == null) {
                final String keys = compatibleKey == null ? "[" + key + "]" : format("[%s, %s]", key, compatibleKey);
                throw new IllegalStateException(format("Unable to find property from any of these keys: %s, description: '%s'", keys, desc));
            }
            return prop;
        }
    }
}
