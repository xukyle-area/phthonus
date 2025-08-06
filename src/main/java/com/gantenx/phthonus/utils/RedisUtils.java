package com.gantenx.phthonus.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Tuple;
import java.util.*;

import com.gantenx.phthonus.enums.Environment;
import com.google.common.base.Joiner;
import com.tiger.exodus.proto.Common.MARKET;
import com.tiger.exodus.quote.proto.QuoteOuterClass;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.Duration;
import redis.clients.jedis.params.SetParams;

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
    
    // ==================== 内部配置类 ====================
    private static class Config {
        public static final int MAX_TOTAL = 100;
        public static final int MAX_IDLE = 20;
        public static final int MIN_IDLE = 5;
        public static final int MAX_WAIT_MILLIS = 3000;
        public static final String DEFAULT_HOST = "localhost";
        public static final int DEFAULT_PORT = 6379;
        public static final int DEFAULT_TIMEOUT = 2000;
        public static final int DEFAULT_DATABASE = 0;
    }

    // ==================== Redis操作类 ====================
    public static class StringOps {
        public static String set(String key, String value) {
            try (Jedis jedis = getJedis()) {
                return jedis.set(key, value);
            }
        }

        public static String get(String key) {
            try (Jedis jedis = getJedis()) {
                return jedis.get(key);
            }
        }
        
        // ... 其他String操作方法将在此处定义 ...
    }

    public static class HashOps {
        public static Long hset(String key, String field, String value) {
            try (Jedis jedis = getJedis()) {
                return jedis.hset(key, field, value);
            }
        }
        
        // ... 其他Hash操作方法将在此处定义 ...
    }

    public static class SetOps {
        // ... Set操作方法将在此处定义 ...
    }

    public static class ListOps {
        // ... List操作方法将在此处定义 ...
    }

    public static class ZSetOps {
        public static Set<Tuple> zrevrangeWithScores(String key, long start, long stop) {
            try (Jedis jedis = getJedis()) {
                return jedis.zrevrangeWithScores(key, start, stop);
            }
        }
        
        // ... 其他ZSet操作方法将在此处定义 ...
    }

    // ==================== Key生成器 ====================
    public static class KeyGenerator {
        private static final String SEPARATOR = "_";
        private static final String QUOTE_PREFIX = "quote";
        private static final String QUOTE24_PREFIX = "quote24h";
        private static final String ASK_PREFIX = "order_book_ask_2";
        private static final String BID_PREFIX = "order_book_bid_2";
        private static final String DIFF_PREFIX = "quote_diff_order_book";
        private static final String CANDLE_CACHE_PREFIX = "candle_cache";
        private static final String TRADE_CACHE_PREFIX = "trade_cache";
        private static final String HISTORY_INDEX_PRICE_PREFIX = "qip";
        
        public static final String UPDATE_ID = "upid";
        public static final String DIFF_ASK_FIELD = "ask_";
        public static final String DIFF_BID_FIELD = "bid_";

        public static String generateRedisQuoteKey(long contractId, MARKET market) {
            return joinKey(QUOTE_PREFIX, contractId, market);
        }

        public static String generateTradeCacheKey(long contractId) {
            return joinKey(TRADE_CACHE_PREFIX, contractId);
        }

        private static String joinKey(Object... parts) {
            return Joiner.on(SEPARATOR).join(parts);
        }
    }

    // ==================== 业务处理方法 ====================
    public static class TradeProcessor {
        public static void processTrades(Set<Tuple> trades) {
            for (Tuple trade : trades) {
                try {
                    processTradeData(trade);
                } catch (Exception e) {
                    log.error("Error processing trade: {}", e.getMessage());
                }
            }
        }

        private static void processTradeData(Tuple trade) throws InvalidProtocolBufferException {
            byte[] bytes = trade.getBinaryElement();
            long timestamp = (long) trade.getScore();
            String formattedTime = TimestampUtils.formatTimestamp(timestamp);

            QuoteOuterClass.TradeInfo tradeInfo = QuoteOuterClass.TradeInfo.parseFrom(bytes);
            logTradeInfo(tradeInfo, formattedTime);
        }

        private static void logTradeInfo(QuoteOuterClass.TradeInfo tradeInfo, String time) {
            log.info("Trade: id: {}, price: {}, volume: {}, isBuyerMaker: {}, Time: {}",
                    tradeInfo.getId(),
                    tradeInfo.getPrice().toString().trim(),
                    tradeInfo.getVolume().toString().trim(),
                    tradeInfo.getIsBuyerMaker(),
                    time);
        }
    }

    public static void main(String[] args) {
        try {
            // 初始化连接
            RedisUtils.init(Environment.AWS1.getRedisCluster(), 6379, null, 0);

            // 使用KeyGenerator生成key
            String key = KeyGenerator.generateTradeCacheKey(2);

            // 使用ZSetOps获取数据
            Set<Tuple> trades = ZSetOps.zrevrangeWithScores(key, 0, 20);
            
            // 使用TradeProcessor处理数据
            TradeProcessor.processTrades(trades);
        } finally {
            RedisUtils.close();
        }

        // // Hash 操作
        // RedisUtils.hset("user:100", "name", "张三");
        // String name = RedisUtils.hget("user:100", "name");
        // System.out.println("user:100 name = " + name);

        // // List 操作
        // RedisUtils.lpush("mylist", "a", "b", "c");
        // System.out.println("mylist lrange: " + RedisUtils.lrange("mylist", 0, -1));

        // // Set 操作
        // RedisUtils.sadd("myset", "x", "y", "z");
        // System.out.println("myset members: " + RedisUtils.smembers("myset"));

        // // ZSet 操作
        // RedisUtils.zadd("myzset", 1.0, "one");
        // RedisUtils.zadd("myzset", 2.0, "two");
        // System.out.println("myzset zrange: " + RedisUtils.zrange("myzset", 0, -1));

        // 关闭连接池
        RedisUtils.close();
    }

    private static final String QUOTE_PREFIX = "quote";
    private static final String QUOTE24_PREFIX = "quote24h";
    private static final String ASK_PREFIX = "order_book_ask_2";
    private static final String BID_PREFIX = "order_book_bid_2";
    private static final String DIFF_PREFIX = "quote_diff_order_book";
    public static final String UPDATE_ID = "upid";
    public static final String DIFF_ASK_FIELD = "ask_";
    public static final String DIFF_BID_FIELD = "bid_";
    private static final String CANDLE_CACHE_PREFIX = "candle_cache";
    private static final String TRADE_CACHE_PREFIX = "trade_cache";
    private static final String HISTORY_INDEX_PRICE_PREFIX = "qip";
    private static final String SEPARATOR = "_";

    public static String generateRedisQuoteKey(long contractId, MARKET market) {
        return Joiner.on(SEPARATOR).join(QUOTE_PREFIX, contractId, market.toString());
    }

    public static String generateRedisQuote24HKey(long contractId, MARKET market) {
        return Joiner.on(SEPARATOR).join(QUOTE24_PREFIX, contractId, market.toString());
    }

    public static String generateOrderBookAskKey(long contractId, MARKET market) {
        return Joiner.on(SEPARATOR).join(ASK_PREFIX, contractId, market.toString());
    }

    public static String generateOrderBookBidKey(long contractId, MARKET market) {
        return Joiner.on(SEPARATOR).join(BID_PREFIX, contractId, market.toString());
    }

    public static String generateDiffOrderBookKey(long contractId, MARKET market) {
        return Joiner.on(SEPARATOR).join(DIFF_PREFIX, contractId, market.toString());
    }

    public static String generateCandleCacheKey(long contractId, int resolution) {
        return Joiner.on(SEPARATOR).join(CANDLE_CACHE_PREFIX, contractId, resolution);
    }

    public static String generateTradeCacheKey(long contractId) {
        return Joiner.on(SEPARATOR).join(TRADE_CACHE_PREFIX, contractId);
    }

    public static String generateHistoryIndexPriceKey(String symbol, long timestamp) {
        return Joiner.on(SEPARATOR).join(HISTORY_INDEX_PRICE_PREFIX, symbol, timestamp);
    }

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
            jedisPool = new JedisPool(config, host, port, Config.DEFAULT_TIMEOUT, password, database);
        } else {
            jedisPool = new JedisPool(config, host, port, Config.DEFAULT_TIMEOUT, null, database);
        }
    }

    /**
     * 使用默认配置初始化
     */
    public static void init() {
        init(Config.DEFAULT_HOST, Config.DEFAULT_PORT, null, Config.DEFAULT_DATABASE);
    }

    /**
     * 获取Jedis连接
     */
    private static Jedis getJedis() {
        if (jedisPool == null) {
            init();
        }
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

    // ==================== String 操作 ====================

    /**
     * 设置字符串值
     */
    public static String set(String key, String value) {
        try (Jedis jedis = getJedis()) {
            return jedis.set(key, value);
        }
    }

    /**
     * 设置字符串值并设置过期时间
     */
    public static String setWithExpire(String key, String value, long seconds) {
        try (Jedis jedis = getJedis()) {
            return jedis.set(key, value, new SetParams().px(seconds * 1000));
        }
    }

    /**
     * 获取字符串值
     */
    public static String get(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.get(key);
        }
    }

    /**
     * 删除键
     */
    public static Long del(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.unlink(key);  // Using unlink instead of del for non-blocking delete
        }
    }

    /**
     * 检查键是否存在
     */
    public static Boolean exists(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.exists(key);
        }
    }

    /**
     * 设置过期时间（以秒为单位）
     */
    public static Boolean expire(String key, long seconds) {
        try (Jedis jedis = getJedis()) {
            return jedis.expire(key, seconds) == 1L;
        }
    }

    /**
     * 获取剩余过期时间
     */
    public static Long ttl(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.ttl(key);
        }
    }

    // ==================== Hash 操作 ====================

    /**
     * 设置哈希表字段值
     */
    public static Long hset(String key, String field, String value) {
        try (Jedis jedis = getJedis()) {
            return jedis.hset(key, field, value);
        }
    }

    /**
     * 批量设置哈希表字段值
     */
    public static String hmset(String key, Map<String, String> hash) {
        try (Jedis jedis = getJedis()) {
            return jedis.hmset(key, hash);
        }
    }

    /**
     * 获取哈希表字段值
     */
    public static String hget(String key, String field) {
        try (Jedis jedis = getJedis()) {
            return jedis.hget(key, field);
        }
    }

    /**
     * 获取哈希表所有字段值
     */
    public static Map<String, String> hgetAll(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.hgetAll(key);
        }
    }

    /**
     * 删除哈希表字段
     */
    public static Long hdel(String key, String... fields) {
        try (Jedis jedis = getJedis()) {
            return jedis.hdel(key, fields);
        }
    }

    /**
     * 检查哈希表字段是否存在
     */
    public static Boolean hexists(String key, String field) {
        try (Jedis jedis = getJedis()) {
            return jedis.hexists(key, field);
        }
    }

    /**
     * 获取哈希表字段数量
     */
    public static Long hlen(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.hlen(key);
        }
    }

    // ==================== List 操作 ====================

    /**
     * 从左侧推入列表
     */
    public static Long lpush(String key, String... values) {
        try (Jedis jedis = getJedis()) {
            return jedis.lpush(key, values);
        }
    }

    /**
     * 从右侧推入列表
     */
    public static Long rpush(String key, String... values) {
        try (Jedis jedis = getJedis()) {
            return jedis.rpush(key, values);
        }
    }

    /**
     * 从左侧弹出列表元素
     */
    public static String lpop(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.lpop(key);
        }
    }

    /**
     * 从右侧弹出列表元素
     */
    public static String rpop(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.rpop(key);
        }
    }

    /**
     * 获取列表指定范围的元素
     */
    public static List<String> lrange(String key, long start, long stop) {
        try (Jedis jedis = getJedis()) {
            return jedis.lrange(key, start, stop);
        }
    }

    /**
     * 获取列表长度
     */
    public static Long llen(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.llen(key);
        }
    }

    /**
     * 获取列表指定位置的元素
     */
    public static String lindex(String key, long index) {
        try (Jedis jedis = getJedis()) {
            return jedis.lindex(key, index);
        }
    }

    // ==================== Set 操作 ====================

    /**
     * 向集合添加元素
     */
    public static Long sadd(String key, String... members) {
        try (Jedis jedis = getJedis()) {
            return jedis.sadd(key, members);
        }
    }

    /**
     * 从集合移除元素
     */
    public static Long srem(String key, String... members) {
        try (Jedis jedis = getJedis()) {
            return jedis.srem(key, members);
        }
    }

    /**
     * 获取集合所有元素
     */
    public static Set<String> smembers(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.smembers(key);
        }
    }

    /**
     * 检查元素是否在集合中
     */
    public static Boolean sismember(String key, String member) {
        try (Jedis jedis = getJedis()) {
            return jedis.sismember(key, member);
        }
    }

    /**
     * 获取集合元素数量
     */
    public static Long scard(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.scard(key);
        }
    }

    /**
     * 获取集合交集
     */
    public static Set<String> sinter(String... keys) {
        try (Jedis jedis = getJedis()) {
            return jedis.sinter(keys);
        }
    }

    /**
     * 获取集合并集
     */
    public static Set<String> sunion(String... keys) {
        try (Jedis jedis = getJedis()) {
            return jedis.sunion(keys);
        }
    }

    // ==================== ZSet 操作 ====================

    /**
     * 向有序集合添加元素
     */
    public static Long zadd(String key, double score, String member) {
        try (Jedis jedis = getJedis()) {
            return jedis.zadd(key, score, member);
        }
    }

    /**
     * 批量向有序集合添加元素
     */
    public static Long zadd(String key, Map<String, Double> scoreMembers) {
        try (Jedis jedis = getJedis()) {
            return jedis.zadd(key, scoreMembers);
        }
    }

    /**
     * 获取有序集合元素分数
     */
    public static Double zscore(String key, String member) {
        try (Jedis jedis = getJedis()) {
            return jedis.zscore(key, member);
        }
    }

    /**
     * 获取有序集合元素排名（从低到高）
     */
    public static Long zrank(String key, String member) {
        try (Jedis jedis = getJedis()) {
            return jedis.zrank(key, member);
        }
    }

    /**
     * 获取有序集合元素排名（从高到低）
     */
    public static Long zrevrank(String key, String member) {
        try (Jedis jedis = getJedis()) {
            return jedis.zrevrank(key, member);
        }
    }

    /**
     * 获取有序集合指定范围的元素（从低到高）
     */
    public static Set<String> zrange(String key, long start, long stop) {
        try (Jedis jedis = getJedis()) {
            return jedis.zrange(key, start, stop);
        }
    }

    /**
     * 获取有序集合指定范围的元素（从高到低）
     */
    public static Set<String> zrevrange(String key, long start, long stop) {
        try (Jedis jedis = getJedis()) {
            return jedis.zrevrange(key, start, stop);
        }
    }

    /**
     * 获取有序集合指定范围的元素和分数（从低到高）
     */
    public static Set<Tuple> zrangeWithScores(String key, long start, long stop) {
        try (Jedis jedis = getJedis()) {
            return jedis.zrangeWithScores(key, start, stop);
        }
    }

    /**
     * 获取有序集合指定范围的元素和分数（从高到低）
     */
    public static Set<Tuple> zrevrangeWithScores(String key, long start, long stop) {
        try (Jedis jedis = getJedis()) {
            return jedis.zrevrangeWithScores(key, start, stop);
        }
    }

    /**
     * 获取有序集合元素数量
     */
    public static Long zcard(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.zcard(key);
        }
    }

    /**
     * 从有序集合移除元素
     */
    public static Long zrem(String key, String... members) {
        try (Jedis jedis = getJedis()) {
            return jedis.zrem(key, members);
        }
    }

    // ==================== 通用操作 ====================

    /**
     * 获取所有匹配的键
     */
    public static Set<String> keys(String pattern) {
        try (Jedis jedis = getJedis()) {
            return jedis.keys(pattern);
        }
    }

    /**
     * 获取键的类型
     */
    public static String type(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.type(key);
        }
    }

    /**
     * 清空当前数据库
     */
    public static boolean clearCurrentDB() {
        try (Jedis jedis = getJedis()) {
            Set<String> keys = jedis.keys("*");
            if (!keys.isEmpty()) {
                jedis.del(keys.toArray(new String[0]));
            }
            return true;
        }
    }

    /**
     * 获取数据库大小
     */
    public static Long getKeyCount() {
        try (Jedis jedis = getJedis()) {
            return (long) jedis.keys("*").size();
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 批量设置键值对
     */
    public static String mset(Map<String, String> keyValues) {
        try (Jedis jedis = getJedis()) {
            String[] keyValueArray = new String[keyValues.size() * 2];
            int i = 0;
            for (Map.Entry<String, String> entry : keyValues.entrySet()) {
                keyValueArray[i++] = entry.getKey();
                keyValueArray[i++] = entry.getValue();
            }
            return jedis.mset(keyValueArray);
        }
    }

    /**
     * 批量获取键值对
     */
    public static List<String> mget(String... keys) {
        try (Jedis jedis = getJedis()) {
            return jedis.mget(keys);
        }
    }

    /**
     * 原子递增
     */
    public static Long incr(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.incr(key);
        }
    }

    /**
     * 原子递减
     */
    public static Long decr(String key) {
        try (Jedis jedis = getJedis()) {
            return jedis.decr(key);
        }
    }

    /**
     * 原子递增指定值
     */
    public static Long incrBy(String key, long increment) {
        try (Jedis jedis = getJedis()) {
            return jedis.incrBy(key, increment);
        }
    }

    /**
     * 原子递减指定值
     */
    public static Long decrBy(String key, long decrement) {
        try (Jedis jedis = getJedis()) {
            return jedis.decrBy(key, decrement);
        }
    }

    /**
     * 检查Redis服务是否可用
     */
    public static boolean isServerAvailable() {
        try (Jedis jedis = getJedis()) {
            String response = jedis.echo("ping");
            return "ping".equals(response);
        } catch (Exception e) {
            log.error("Redis server unavailable", e);
            return false;
        }
    }

    /**
     * 获取Redis服务器基本信息
     */
    public static Map<String, String> getServerInfo() {
        try (Jedis jedis = getJedis()) {
            Map<String, String> info = new HashMap<>();
            info.put("connected_clients", String.valueOf(jedis.clientList().split("\n").length));
            info.put("total_keys", String.valueOf(getKeyCount()));
            info.put("status", isServerAvailable() ? "connected" : "disconnected");
            return info;
        }
    }
}