package com.gantenx.phthonus.service;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import com.gantenx.phthonus.constants.Constant;
import com.gantenx.phthonus.enums.Environment;
import com.gantenx.phthonus.enums.Market;
import com.gantenx.phthonus.enums.Symbol;
import com.gantenx.phthonus.model.websocket.RealTimeQuote;
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

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Environment.AWS2.getKafkaBootstrapServers());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        this.producer = new KafkaProducer<>(props, new StringSerializer(), new ProtoSerializer<>());
    }

    public void send(String kafkaTopic, String mqttTopic, Object msg) {
        ApiPub.PublishMessage publishMessage =
                ApiPub.PublishMessage.newBuilder().setMqttTopic(mqttTopic).setTimestamp(System.currentTimeMillis())
                        .setPayload(ByteString.copyFrom(JsonUtils.toJson(msg), StandardCharsets.UTF_8)).build();

        // producer.send(new ProducerRecord<>(kafkaTopic, publishMessage), (metadata, ex) -> {
        //     if (ex != null) {
        //         log.error("Failed to send message to Kafka", ex);
        //     } else {
        //         // log.info("Message sent to topic: {}, message: {}", metadata.topic(), publishMessage);
        //     }
        // });
    }

    private static class Holder {
        private static final KafkaSender INSTANCE = new KafkaSender();
    }

    public static KafkaSender getInstance() {
        return Holder.INSTANCE;
    }


    public final static String OTHER_MQTT_TOPIC = "api/trade/account/94167306/?lang=lang_neutral";
    public final static String KAFKA_TOPIC = "api";
    public static void main(String[] args) {

        KafkaSender sender = KafkaSender.getInstance();

        for (int i = 0; i < 10000000; i++) {
            RealTimeQuote realTimeQuote = new RealTimeQuote();
            realTimeQuote.setSymbol(Symbol.BTC_USDT);
            realTimeQuote.setTimestamp(System.currentTimeMillis());
            realTimeQuote.setAsk(String.valueOf(1093363981));
            realTimeQuote.setLast(String.valueOf(1093363982));
            realTimeQuote.setBid(String.valueOf(1093363983));
            realTimeQuote.setMarket(Market.BINANCE);
            sender.send(Constant.KAFKA_TOPIC, Constant.OTHER_MQTT_TOPIC, realTimeQuote);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } ;
            System.out.println(realTimeQuote);
        }

    }
}
