package com.gantenx.phthonus.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Redis工具类使用示例
 */
public class RedisExample {

    public static void main(String[] args) {
        // 初始化Redis连接（使用默认配置）
        RedisUtils.init("localhost", 6379, null, 0);
        
        System.out.println("=== Redis工具类使用示例 ===");
        
        // 测试连接
        String pingResult = RedisUtils.ping();
        System.out.println("连接测试: " + pingResult);
        
        // String 操作示例
        stringOperations();
        
        // Hash 操作示例
        hashOperations();
        
        // List 操作示例
        listOperations();
        
        // Set 操作示例
        setOperations();
        
        // ZSet 操作示例
        zsetOperations();
        
        // 清理测试数据
        cleanup();
        
        // 关闭连接
        RedisUtils.close();
    }
    
    /**
     * String 操作示例
     */
    private static void stringOperations() {

    }
    
    /**
     * Hash 操作示例
     */
    private static void hashOperations() {
        System.out.println("\n--- Hash 操作示例 ---");
        
        // 设置单个字段
        RedisUtils.hset("user:1", "name", "李四");
        RedisUtils.hset("user:1", "age", "25");
        RedisUtils.hset("user:1", "email", "lisi@example.com");
        
        // 批量设置字段
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("city", "北京");
        userInfo.put("job", "工程师");
        RedisUtils.hmset("user:1", userInfo);
        
        // 获取单个字段
        String name = RedisUtils.hget("user:1", "name");
        System.out.println("用户名: " + name);
        
        // 获取所有字段
        Map<String, String> allFields = RedisUtils.hgetAll("user:1");
        System.out.println("用户信息: " + allFields);
        
        // 检查字段是否存在
        Boolean hasAge = RedisUtils.hexists("user:1", "age");
        System.out.println("是否有年龄字段: " + hasAge);
    }
    
    /**
     * List 操作示例
     */
    private static void listOperations() {
        System.out.println("\n--- List 操作示例 ---");
        
        // 从左侧推入元素
        RedisUtils.lpush("queue:orders", "order:001", "order:002", "order:003");
        
        // 从右侧推入元素
        RedisUtils.rpush("queue:orders", "order:004");
        
        // 获取列表长度
        Long length = RedisUtils.llen("queue:orders");
        System.out.println("队列长度: " + length);
        
        // 获取指定范围的元素
        java.util.List<String> orders = RedisUtils.lrange("queue:orders", 0, -1);
        System.out.println("所有订单: " + orders);
        
        // 从左侧弹出元素
        String firstOrder = RedisUtils.lpop("queue:orders");
        System.out.println("第一个订单: " + firstOrder);
    }
    
    /**
     * Set 操作示例
     */
    private static void setOperations() {
        System.out.println("\n--- Set 操作示例 ---");
        
        // 添加元素到集合
        RedisUtils.sadd("tags:article:1", "Java", "Redis", "Spring", "Database");
        RedisUtils.sadd("tags:article:2", "Java", "Kafka", "Microservices");
        
        // 获取集合所有元素
        Set<String> tags1 = RedisUtils.smembers("tags:article:1");
        System.out.println("文章1标签: " + tags1);
        
        // 检查元素是否在集合中
        Boolean hasJava = RedisUtils.sismember("tags:article:1", "Java");
        System.out.println("文章1是否有Java标签: " + hasJava);
        
        // 获取集合交集
        Set<String> commonTags = RedisUtils.sinter("tags:article:1", "tags:article:2");
        System.out.println("共同标签: " + commonTags);
    }
    
    /**
     * ZSet 操作示例
     */
    private static void zsetOperations() {
        System.out.println("\n--- ZSet 操作示例 ---");
        
        // 添加元素到有序集合
        RedisUtils.zadd("leaderboard", 100.0, "player1");
        RedisUtils.zadd("leaderboard", 85.5, "player2");
        RedisUtils.zadd("leaderboard", 120.0, "player3");
        RedisUtils.zadd("leaderboard", 95.0, "player4");
        
        // 获取元素分数
        Double score = RedisUtils.zscore("leaderboard", "player1");
        System.out.println("player1分数: " + score);
        
        // 获取元素排名（从高到低）
        Long rank = RedisUtils.zrevrank("leaderboard", "player1");
        System.out.println("player1排名: " + (rank != null ? rank + 1 : "未找到"));
        
        // 获取前3名（从高到低）
        Set<String> top3 = RedisUtils.zrevrange("leaderboard", 0, 2);
        System.out.println("前3名: " + top3);
        
        // 获取所有元素和分数
        Set<redis.clients.jedis.Tuple> allScores = RedisUtils.zrevrangeWithScores("leaderboard", 0, -1);
        System.out.println("所有玩家分数:");
        for (redis.clients.jedis.Tuple tuple : allScores) {
            System.out.println("  " + tuple.getElement() + ": " + tuple.getScore());
        }
    }
    
    /**
     * 清理测试数据
     */
    private static void cleanup() {
        System.out.println("\n--- 清理测试数据 ---");
        
        String[] keysToDelete = {
            "user:1:name", "user:1:session", "user:1", "counter",
            "queue:orders", "tags:article:1", "tags:article:2", "leaderboard"
        };
        
        for (String key : keysToDelete) {
            RedisUtils.del(key);
        }
        
        System.out.println("测试数据已清理");
    }
} 