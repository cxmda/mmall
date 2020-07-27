package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenqiang
 * @create 2020-07-22 15:32
 */
public class RedisShardedPool {
    //ShardedJedis连接池
    private static ShardedJedisPool jedisPool;
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

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    private static String redis1Password = PropertiesUtil.getProperty("redis1.password");

    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));
    private static String redis2Password = PropertiesUtil.getProperty("redis2.password");

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        //连接耗尽时是否阻塞，为false时抛出异常，为true时阻塞直到超时，默认为true
        config.setBlockWhenExhausted(true);

        JedisShardInfo info1 = new JedisShardInfo(redis1Ip, redis1Port, 1000 * 2);
        info1.setPassword(redis1Password);
        JedisShardInfo info2 = new JedisShardInfo(redis2Ip, redis2Port, 1000 * 2);
        info2.setPassword(redis2Password);
        List<JedisShardInfo> jedisShardInfoList = new ArrayList<>();
        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        jedisPool = new ShardedJedisPool(config, jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static {
        initPool();
    }

    public static ShardedJedis getJedis() {
        return jedisPool.getResource();
    }

    public static void returnResource(ShardedJedis jedis) {
        jedisPool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis) {
        jedisPool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = jedisPool.getResource();

        for (int i = 0; i < 10; i++) {
            jedis.set("key" + i, "value" + i);
        }
        returnResource(jedis);

        //临时调用，销毁连接池中的所有连接
        //jedisPool.destroy();
        System.out.println("program is end");
    }
}
