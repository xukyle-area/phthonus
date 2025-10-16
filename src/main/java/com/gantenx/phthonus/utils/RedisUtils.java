package com.gantenx.phthonus.utils;

import java.time.Duration;
import java.util.Set;
import com.gantenx.phthonus.enums.Environment;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;

/**
 * Redis工具类
 * <p>
 * 支持多种Redis数据结构操作:
 * - String: 字符串
 * - Hash: 哈希表
 * - List: 列表
 * - Set: 集合
 * - ZSet: 有序集合
 * <p>
 * 依赖:
 * - redis.clients:jedis:4.3.1
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisUtils {
    private static final int DEFAULT_TIMEOUT = 2000;

    private static JedisPool jedisPool;

    /**
     * 初始化Redis连接池
     */
    public static void init(String host, int port, String password, int database) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(20);
        config.setMinIdle(5);
        config.setMaxWait(Duration.ofMillis(3000));
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);

        if (password != null && !password.isEmpty()) {
            jedisPool = new JedisPool(config, host, port, DEFAULT_TIMEOUT, password, database);
        } else {
            jedisPool = new JedisPool(config, host, port, DEFAULT_TIMEOUT, null, database);
        }
    }

    /**
     * 获取Jedis连接
     */
    private static Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 关闭连接池
     */
    public static void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    public static void main(String[] args) {
        try {
            // 初始化连接
            RedisUtils.init(Environment.AWS1.getRedisCluster(), 6379, null, 0);

            // 使用KeyGenerator生成key
            String key = RedisKeyGenerator.generateTradeCacheKey(2);

            // 删除前数量
            Long before;

            Set<Tuple> oldTuples;
            try (Jedis jedis = getJedis()) {
                before = jedis.zcard(key);
                System.out.println("删除前数量: " + before);
                oldTuples = jedis.zrangeWithScores(key, 0, 1000);
                if (!oldTuples.isEmpty()) {
                    byte[][] members = oldTuples.stream().map(Tuple::getBinaryElement).toArray(byte[][]::new);
                    jedis.zrem(key.getBytes(), members);
                }
                // 删除后数量
                Long after = jedis.zcard(key);
                System.out.println("删除后数量: " + after);
            }
        } finally {
            RedisUtils.close();
        }
    }
}
