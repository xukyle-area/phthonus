package com.gantenx.phthonus.redis;

import com.gantenx.phthonus.common.Market;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;


@Slf4j
public class RedisViewer {

    // 使用 pod ip
    private final static String NODE_STR = "yax-dt2-sandbox01-rediscluster-main0-1.exodushk.com:6379";
    private final static String PASSWORD = "Jebut90FgKpwKBm";
    private static final String SEPARATOR = "_";
    private static final String QUOTE_PREFIX = "quote";

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        RedisViewer redisViewer = new RedisViewer();
        redisViewer.handle();
    }

    private void handle() {
        try (JedisCluster redis = RedisClusterConnector.createCluster(NODE_STR, PASSWORD)) {
            this.use(redis);
            this.printRealtimePrice(41L, Market.BINANCE, redis);
        } catch (Exception e) {
            log.error("Connect error: {}", e.getMessage());
        }
    }

    private void use(JedisCluster redis) {
        log.info(redis.get("rule_expression:YAX_HK_Risk_Rating_Nationality_Rule"));
        log.info(redis.get("rule_expression:YAX_HK_Risk_Rating_Residence_Rule"));
        log.info(redis.get("rule_expression:YAX_HK_Risk_Rating_Industry_Rule"));
    }

    public void printRealtimePrice(long contractId, Market market, JedisCluster redis) {
        String key = generateRedisQuoteKey(contractId, market);
        log.info(redis.hget(key, "ask"));
        log.info(redis.hget(key, "bid"));
        log.info(redis.hget(key, "last"));
        log.info(redis.hget(key, "change24h"));
        log.info(redis.hget(key, "changePercent24h"));
    }

    public static String generateRedisQuoteKey(long contractId, Market market) {
        return Joiner.on(SEPARATOR).join(QUOTE_PREFIX, contractId, market.toString());
    }

}
