package com.amazonaws.example.serverless.ws.cache;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created by menzelmi on 22/02/2017.
 */
public class SyncMemcachedCache extends AbstractSyncCache {

    private MemcachedClient client;

    private String endpoint;

    private int timeout;

    private TimeUnit timeoutUnit;

    private LambdaLogger logger;


    public SyncMemcachedCache(String endpoint, int timeout, TimeUnit timeoutUnit, LambdaLogger logger) {
        this.endpoint = endpoint;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
        this.logger = logger;
    }


    public MemcachedClient getClient() throws IOException {
        if(client == null)
            client = new MemcachedClient(
                    AddrUtil.getAddresses(endpoint)
            );
        return client;
    }

    public int getTimeout() {
        return timeout;
    }

    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    public LambdaLogger getLogger() {
        return logger;
    }

    @Override
    public Object get(String key) {
        try {
            return getClient().get(key);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public boolean set(String key, int expiration, Object value) {
        try {
            return getClient().set(key, expiration, value).get(timeout, timeoutUnit);
        } catch (Exception e) {
            logger.log(String.format("Exception or timeout when storing key %s to cache. %s", key, e.getMessage()));
            return false;
        }
    }

    @Override
    public Object getOrElse(String key, int expiration, Object value) {
        return Optional.ofNullable(get(key)).orElseGet(() -> {
            set("result", 15 * 60, value);
            return value;
        });
    }

    @Override
    public Object getOrElse(String key, int expiration, Supplier<Object> value) {
        return Optional.ofNullable(get(key)).orElseGet(() -> {
            Object val = value.get();
            set("result", 15 * 60, val);
            return val;
        });
    }

    @Override
    public boolean delete(String key) {
        try {
            return getClient().delete(key).get(timeout, timeoutUnit);
        } catch (Exception e) {
            logger.log(String.format("Exception or timeout while deleting key %s in cache. %s", key, e.getMessage()));
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        getClient().shutdown();
    }
}
