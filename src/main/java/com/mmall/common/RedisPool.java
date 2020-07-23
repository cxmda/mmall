package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author chenqiang
 * @create 2020-07-22 15:32
 */
public class RedisPool {
    //jedis连接池
    private static JedisPool jedisPool;
    //最大连接数
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));
    //在jedisPool中最大的idle状态(空闲的)的jedis实例的个数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "20"));
    //在jedisPool中最小的idle状态(空闲的)的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "20"));

    //在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的。
    private static Boolean testOnBorrow = true;
    //在return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedisPool的jedis实例肯定是可以用的。
    private static Boolean testOnReturn = true;

    private static String redisIp = PropertiesUtil.getProperty("redis.ip");
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));
    private static String redisPassword = PropertiesUtil.getProperty("redis.password");

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        //连接耗尽时是否阻塞，为false时抛出异常，为true时阻塞直到超时，默认为true
        config.setBlockWhenExhausted(true);
        jedisPool = new JedisPool(config, redisIp, redisPort, 1000 * 2, redisPassword);
    }

    static {
        initPool();
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }

    public static void returnResource(Jedis jedis) {
        jedisPool.returnResource(jedis);
    }

    public static void returnBrokenResource(Jedis jedis) {
        jedisPool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = jedisPool.getResource();
        jedis.set("chen", "chen");
        returnResource(jedis);

        //临时调用，销毁连接池中的所有连接
        jedisPool.destroy();
        System.out.println("program is end");
    }
}
