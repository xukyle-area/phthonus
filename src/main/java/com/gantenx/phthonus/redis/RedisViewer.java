package com.gantenx.phthonus.redis;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;


@Slf4j
public class RedisViewer {

    // 使用 pod ip
    private final static String NODE_STR = "10.251.90.59:6379";
    private final static String PASSWORD = null;

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        RedisViewer redisViewer = new RedisViewer();
        redisViewer.handle();
    }

    private void handle() {
        try (JedisCluster redis = RedisClusterConnector.createCluster(NODE_STR, PASSWORD)) {
            this.use(redis);
        } catch (Exception e) {
            log.error("Connect error: {}", e.getMessage());
        }
    }

    private void use(JedisCluster redis) {
        log.info(redis.get("rule_expression:YAX_HK_Risk_Rating_Nationality_Rule"));
        log.info(redis.get("rule_expression:YAX_HK_Risk_Rating_Residence_Rule"));
        log.info(redis.get("rule_expression:YAX_HK_Risk_Rating_Industry_Rule"));
    }
}
