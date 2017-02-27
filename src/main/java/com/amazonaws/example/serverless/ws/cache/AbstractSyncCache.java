package com.amazonaws.example.serverless.ws.cache;

import java.util.function.Supplier;

/**
 * Created by menzelmi on 22/02/2017.
 */
public abstract class AbstractSyncCache implements CacheApi {

    @Override
    public boolean set(String key, int expiration, Supplier<Object> value) {
        return set(key, expiration, value.get());
    }


    @Override
    public Object getOrElse(String key, int expiration, Supplier<Object> value) {
        return getOrElse(key, expiration, value.get());
    }

}
