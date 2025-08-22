package com.gantenx.phthonus.service;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import com.gantenx.phthonus.enums.Environment;
import com.gantenx.phthonus.utils.JsonUtils;
import com.gantenx.phthonus.utils.ProtoSerializer;
import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessageV3;
import com.tiger.exodus.api_pub.proto.ApiPub;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaSender {

    private final Producer<String, GeneratedMessageV3> producer;

    private KafkaSender() {
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Environment.AWS1.getKafkaBootstrapServers());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        this.producer = new KafkaProducer<>(props, new StringSerializer(), new ProtoSerializer<>());
    }

    public void send(String kafkaTopic, String mqttTopic, Object msg) {
        ApiPub.PublishMessage publishMessage =
                ApiPub.PublishMessage.newBuilder().setMqttTopic(mqttTopic).setTimestamp(System.currentTimeMillis())
                        .setPayload(ByteString.copyFrom(JsonUtils.toJson(msg), StandardCharsets.UTF_8)).build();

        producer.send(new ProducerRecord<>(kafkaTopic, publishMessage), (metadata, ex) -> {
            if (ex != null) {
                log.error("Failed to send message to Kafka", ex);
            } else {
                log.info("Message sent to topic: {}, message: {}", metadata.topic(), publishMessage);
            }
        });
    }

    private static class Holder {
        private static final KafkaSender INSTANCE = new KafkaSender();
    }

    public static KafkaSender getInstance() {
        return Holder.INSTANCE;
    }
}
