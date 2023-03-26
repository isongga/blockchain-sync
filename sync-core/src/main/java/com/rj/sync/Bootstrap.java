package com.rj.sync;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.rj.sync.event.codec.EventCodec;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.*;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Bootstrap {
    public static void main(String[] args) throws MalformedURLException {
        log.info("开始启动。。。。");
        Vertx vertx = Vertx.vertx(new VertxOptions()
                .setMaxEventLoopExecuteTime(10_000_000_000L)
                .setWarningExceptionTime(5_000_000_000L)
                .setHAEnabled(false)
                .setPreferNativeTransport(true));

        // 注册编解码器
        vertx.eventBus().registerCodec(EventCodec.INSTANCE);

        ConfigRetriever configRetriever = ConfigRetriever.create(vertx);

        getConfig(configRetriever)
                .compose(conf -> getDbClient(vertx, conf))
                .onSuccess(client -> {
                    vertx.deployVerticle(new BlockCacheVerticle(client));
                });


    HttpClient httpClient = vertx.createHttpClient(new HttpClientOptions()
            .setConnectTimeout(3000)
            .setSsl(false)
            .setKeepAlive(true)
            .setIdleTimeout(120)
            .setReusePort(true)
            .setReuseAddress(true)
            .setKeepAliveTimeout(120)
            .setTcpFastOpen(true)
            .setTcpNoDelay(true)
            .setTcpQuickAck(true)
            .setMaxPoolSize(2));

        httpClient.request(HttpMethod.GET, 8080, "eth.getblock.io", "/12925c6c-f0ad-41f8-8cce-fec20ed82fed/mainnet/")
                .compose(request -> {
                            request.setChunked(true);
                            for (int i = 0; i < 10; i++) {
                                request.write("client-chunk-" + i);
                            }
                            request.end();
                            return request.response().compose(resp -> {
                                System.out.println("Got response " + resp.statusCode());
                                return resp.body();
                            });
                        }
                )
                .onSuccess(body -> System.out.println("Got data " + body.toString("ISO-8859-1")))
                .onFailure(Throwable::printStackTrace);



        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Content-Type", "application/json");
        JsonRpcHttpClient jsonRpcHttpClient = new JsonRpcHttpClient(new URL("https://eth.getblock.io/12925c6c-f0ad-41f8-8cce-fec20ed82fed/mainnet/"), headers);

        Future<String> deplySync = vertx.deployVerticle(new SyncNewestBlockVerticle(jsonRpcHttpClient));


//        CompositeFuture future = CompositeFuture.join(
//                deplySync,
//                deplyBlkCache);
//        future.onSuccess((f) -> log.info("Startup complete!"));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            vertx.close().onComplete((result) -> System.out.println("Shutdown complete!"));
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }





    private static Future<JsonObject> getConfig(ConfigRetriever configRetriever) {
        Promise<JsonObject> promise = Promise.promise();
        configRetriever.getConfig(ar -> {
            if (ar.succeeded()) {
                promise.complete(ar.result());
            } else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }

    private static Future<MySQLPool> getDbClient(Vertx vertx, JsonObject config) {
        Promise<MySQLPool> promise = Promise.promise();
        MySQLConnectOptions dbConnOptions = new MySQLConnectOptions()
                .setHost(config.getString("host"))
                .setPort(config.getInteger("port"))
                .setDatabase(config.getString("database"))
                .setUser(config.getString("user"))
                .setPassword(config.getString("password"));

        PoolOptions poolOptions = new PoolOptions()
                .setShared(true)
                .setName("mysql-pool")
                .setMaxSize(5);

        MySQLPool client = MySQLPool.pool(vertx, dbConnOptions, poolOptions);
        promise.complete(client);
        return promise.future();
    }
}
