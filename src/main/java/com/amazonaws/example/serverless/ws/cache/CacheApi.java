package com.amazonaws.example.serverless.ws.cache;

import java.util.function.Supplier;

/**
 * Created by menzelmi on 22/02/2017.
 */
public interface CacheApi extends AutoCloseable {

    Object get(String key);

    boolean set(String key, int expiration, Object value);

    boolean set(String key, int expiration, Supplier<Object> value);

    Object getOrElse(String key, int expiration, Object value);

    Object getOrElse(String key, int expiration, Supplier<Object> value);

    boolean delete(String key);

}
