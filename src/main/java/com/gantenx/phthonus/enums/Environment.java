package com.gantenx.phthonus.enums;

import lombok.Getter;

@Getter
public enum Environment {
    AWS1(
            "aws1",
            "yax-dt2-kafka0-1.exodushk.com:9092,yax-dt2-kafka0-2.exodushk.com:9092",
            "yax-dt2-redis-cluster-0.exodushk.com"),
    AWS2(
            "aws2",
            "10.251.64.224:9092",
            "redis-cluster.exodus-aws2.svc.cluster.local"),
    SANDBOX(
            "sandbox",
            "yax-dt2-sandbox01-kafka-main0-1.exodushk.com:9092",
            ""
            );

    private final String value;
    private final String kafkaBootstrapServers;
    private final String redisCluster;

    Environment(String value, String kafkaBootstrapServers, String redisCluster) {
        this.value = value;
        this.kafkaBootstrapServers = kafkaBootstrapServers;
        this.redisCluster = redisCluster;
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
