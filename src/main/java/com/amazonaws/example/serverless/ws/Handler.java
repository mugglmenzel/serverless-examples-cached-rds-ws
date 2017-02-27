package com.amazonaws.example.serverless.ws;

import com.amazonaws.example.serverless.ws.cache.CacheApi;
import com.amazonaws.example.serverless.ws.cache.SyncMemcachedCache;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class Handler implements RequestHandler<Request, Response> {

    public Response handleRequest(Request input, Context context) {
        context.getLogger().log("Environment variables:");
        for (String env : System.getenv().keySet())
            context.getLogger().log(String.format("  %s = %s", env, System.getenv(env)));

        String response = "";

        try (
                CacheApi cache = new SyncMemcachedCache(
                        System.getenv("memcachedEndpoint"), 1, TimeUnit.SECONDS, context.getLogger()
                )
        ) {
            Object cached = cache.get("result");
            context.getLogger().log(String.format("Retrieved value from cache: %s", cached));
            response = (String) cache.getOrElse("result", 15 * 60, () -> {
                Long currentEpochTime = System.currentTimeMillis();
                try (
                        Connection conn = DriverManager.getConnection(
                                String.format("jdbc:postgresql://%s/%s", System.getenv("postgresEndpoint"), System.getenv("postgresDatabase")),
                                System.getenv("postgresUser"),
                                System.getenv("postgresPassword")
                        )
                ) {
                    Statement stmt = conn.createStatement();
                    ResultSet resultSet = stmt.executeQuery("SELECT EXTRACT(EPOCH FROM NOW())");
                    if (resultSet.next()) {
                        context.getLogger().log(String.format("Successfully executed query.  Result: %s", resultSet.getLong(1)));
                        currentEpochTime = resultSet.getLong(1) * 1000L;
                    }
                } catch (Exception e) {
                    context.getLogger().log(String.format("Exception during access to RDS. %s", e.getMessage()));
                }
                return String.format("My message at %s", currentEpochTime);
            });
        } catch (Exception e) {
            context.getLogger().log(String.format("Exception during access to ElastiCache. %s", e.getMessage()));
        }

        return new Response("200", serialize(new ResponseBody(String.format("Message at %s is: %s", System.currentTimeMillis(), response), input)));
    }


    public String serialize(Object obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

}
