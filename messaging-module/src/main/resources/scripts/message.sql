CREATE TABLE IF NOT EXISTS message_outbox (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT "primary key",
    routing_key VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'routing key',
    exchange VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'name of exchange',
    payload VARCHAR(1000) NOT NULL DEFAULT '' COMMENT 'payload',
    status VARCHAR(25) NOT NULL DEFAULT '' COMMENT 'delivery status',
    delivery_time TIMESTAMP NULL DEFAULT NULL COMMENT 'delivery time',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'when the record is created',
    create_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who created this record',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'when the record is updated',
    update_by VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'who updated this record',
    is_del TINYINT NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted'
) engine=InnoDB comment 'Message Outbox';