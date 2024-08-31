package org.example.registration;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class RedisService {
    GenericObjectPool<StatefulRedisConnection<String, String>> redisConnectionPool;

    public RedisService() {
        RedisURI redisURI = RedisURI.Builder.redis("127.0.0.1", 6379).build();
        RedisClient redisClient = RedisClient.create(redisURI);
        try {
            GenericObjectPoolConfig<StatefulRedisConnection<String, String>> poolConfig = new GenericObjectPoolConfig<>();
            poolConfig.setMaxTotal(20);
            poolConfig.setMaxIdle(5);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);
            redisConnectionPool = ConnectionPoolSupport.createGenericObjectPool(redisClient::connect, poolConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T executeSync(SyncCommandCallback<T> command) {
        try(StatefulRedisConnection<String, String> connection = redisConnectionPool.borrowObject()) {
            connection.setAutoFlushCommands(true);
            RedisCommands<String, String> syncCommands = connection.sync();
            return command.doInConnection(syncCommands);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Long sadd(String k, String v) {
        return executeSync(commands -> commands.sadd(k, v));
    }

    public String spop(String k) {
        return executeSync(commands -> commands.spop(k));
    }

    public Long exists(String k) {
        return executeSync(commands -> commands.exists(k));
    }

    public String set(String k, String v) {
        return executeSync(command -> command.set(k, v));
    }

    public String get(String k) {
        return executeSync(command -> command.get(k));
    }

    public Long delete(String k) {
        return executeSync(command -> command.del(k));
    }

    public boolean expire(String k, long timeout) {
        return executeSync(commands -> commands.expire(k, timeout));
    }
}
