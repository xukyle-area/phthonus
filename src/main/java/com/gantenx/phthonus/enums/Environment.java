package com.gantenx.phthonus.enums;

import lombok.Getter;

@Getter
public enum Environment {
    AWS1("aws1", "yax-dt2-kafka0-1.exodushk.com:9092,yax-dt2-kafka0-2.exodushk.com:9092"),
    AWS2("aws2", "10.251.64.235:9092"),
    SANDBOX("sandbox", "yax-dt2-sandbox01-kafka-main0-1.exodushk.com:9092");

    private final String value;
    private final String kafkaBootstrapServers;

    Environment(String value, String kafkaBootstrapServers) {
        this.value = value;
        this.kafkaBootstrapServers = kafkaBootstrapServers;
    }

    public static Environment fromValue(String value) {
        for (Environment environment : Environment.values()) {
            if (environment.value.equalsIgnoreCase(value)) {
                return environment;
            }
        }
        throw new IllegalArgumentException("Unknown environment: " + value);
    }
}
