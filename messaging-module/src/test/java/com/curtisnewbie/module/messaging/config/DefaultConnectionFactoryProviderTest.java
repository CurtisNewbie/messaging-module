package com.curtisnewbie.module.messaging.config;

import com.curtisnewbie.module.messaging.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.amqp.rabbit.connection.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.core.env.*;

/**
 * @author yongj.zhuang
 */
@Slf4j
@SpringBootTest(classes = TestBaseConfig.class)
public class DefaultConnectionFactoryProviderTest {

    @Autowired
    private Environment environment;

    @Test
    public void should_parse_environment_properties() {
        final DefaultConnectionFactoryProvider.ConnProp connProp = new DefaultConnectionFactoryProvider.ConnProp(environment);
        Assertions.assertNotNull(connProp);
        Assertions.assertNotNull(connProp.getHost());
        Assertions.assertNotNull(connProp.getPassword());
        Assertions.assertNotNull(connProp.getUsername());
        Assertions.assertNotNull(connProp.getPublisherConfirmType());
        Assertions.assertNotNull(connProp.getVirtualHost());
        Assertions.assertEquals(connProp.getPublisherConfirmType(), CachingConnectionFactory.ConfirmType.CORRELATED);
        log.info("ConnProp: {}", connProp);
    }
}
