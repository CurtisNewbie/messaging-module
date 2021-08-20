package com.curtisnewbie.module.messaging.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;

import java.util.Arrays;
import java.util.Objects;

/**
 * <p>
 * Extension of {@code CorrelationData} with concept of namespace
 * </p>
 * <p>
 * The original namespace and id can be later extracted using {@link #extract(CorrelationData)}
 * </p>
 * <p>
 * For example:
 * </p>
 * <pre>
 * {@code
 * // set the correlationData when we publish messages
 * setCorrelationData(new NamespaceCorrelationData("access-log", "123:345:456"))
 *
 * // extract namespace and id in callback for publisher-confirm
 * NamespaceCorrelationData.NamespaceAndId nid = NamespaceCorrelationData.extract(correlationData);
 * log.info("namespace: {}, id: {}", nid.getNamespace(), nid.getId());
 * }
 * </pre>
 *
 * @author yongjie.zhuang
 */
@Slf4j
public class NamespaceCorrelationData extends CorrelationData {

    /**
     * Construct a {@code NamespaceCorrelationData}
     *
     * @param namespace namespace
     * @param id        id
     */
    public NamespaceCorrelationData(String namespace, String id) {
        Objects.requireNonNull(namespace);
        Objects.requireNonNull(id);
        setId(constructId(namespace, id));
    }

    private static String constructId(String namespace, String id) {
        return namespace + ":" + id;
    }

    /**
     * Extract namespace and id
     *
     * @return null if unable to parse the given correlation_id
     */
    public static NamespaceAndId extract(CorrelationData cd) {
        String cid = cd.getId();
        String[] ca = cid.split(":");
        if (ca.length < 2) {
            log.error("{} correlation_id malformed, unable to parse it", NamespaceCorrelationData.class.getSimpleName());
            return new NamespaceAndId(null, null);
        }
        String namespace = ca[0];
        String id = String.join(":", Arrays.copyOfRange(ca, 1, ca.length));
        return new NamespaceAndId(namespace, id);
    }

    /**
     * Container of namespace and id
     */
    @Data
    @AllArgsConstructor
    public static class NamespaceAndId {
        private final String namespace;
        private final String id;
    }

}
