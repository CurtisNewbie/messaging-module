package com.curtisnewbie.module.messaging.tracing;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Config for tracing
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Data
@Configuration
public class MessageTracingConfig {

    public static final String IS_ENABLED_PROP_KEY = "messaging-module.tracing.enabled";

    @Value("${" + IS_ENABLED_PROP_KEY + ":false}")
    private boolean isEnabled;

    @PostConstruct
    void onInit() {
        if (!isEnabled)
            log.info("Log-tracing for messages is disabled, enabled it by setting '{}=true', note that it does affect header exchange",
                    IS_ENABLED_PROP_KEY);
    }
}
