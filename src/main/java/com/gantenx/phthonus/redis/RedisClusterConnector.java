package com.gantenx.phthonus.redis;

import redis.clients.jedis.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RedisClusterConnector {
    private final static int timeout = 2000;

    public static JedisCluster createCluster(String nodeStr, String password) {
        Set<HostAndPort> nodes = parseHostAndPort(nodeStr);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(50);
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(2);

        DefaultJedisClientConfig.Builder builder = DefaultJedisClientConfig.builder();
        builder.connectionTimeoutMillis(timeout).socketTimeoutMillis(timeout)
                .blockingSocketTimeoutMillis(timeout).clientName("my-java-client").build();
        if (Objects.nonNull(password) && !password.isEmpty()) {
            builder.password(password);
        }
        JedisClientConfig clientConfig = builder.build();

        return new JedisCluster(nodes, clientConfig);
    }

    private static Set<HostAndPort> parseHostAndPort(String nodesStr) {
        Set<HostAndPort> nodes = new HashSet<>();
        Arrays.stream(nodesStr.split(","))
                .map(String::trim)
                .filter(s -> s.contains(":"))
                .forEach(s -> {
                    String[] parts = s.split(":");
                    nodes.add(new HostAndPort(parts[0], Integer.parseInt(parts[1])));
                });
        return nodes;
    }

}
