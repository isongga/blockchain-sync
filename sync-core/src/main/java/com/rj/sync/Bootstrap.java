package com.rj.sync;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.rj.sync.event.codec.EventCodec;
import com.rj.sync.model.Block;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.*;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLConnection;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bootstrap {
    public static void main(String[] args) throws MalformedURLException {
        Vertx vertx = Vertx.vertx(new VertxOptions()
                .setMaxEventLoopExecuteTime(10_000_000_000L)
                .setWarningExceptionTime(5_000_000_000L)
                .setHAEnabled(false)
                .setPreferNativeTransport(true));

        // 注册编解码器
        vertx.eventBus().registerCodec(EventCodec.INSTANCE);

        ConfigRetriever configRetriever = ConfigRetriever.create(vertx);

        configRetriever.getConfig(ar -> {
            if (ar.succeeded()) {

                JsonObject config = ar.result();

                MySQLConnectOptions dbConnOptions = new MySQLConnectOptions()
                        .setHost(config.getString("host"))
                        .setPort(config.getInteger("port"))
                        .setDatabase(config.getString("database"))
                        .setUser(config.getString("user"))
                        .setPassword(config.getString("password"));

                PoolOptions poolOptions = new PoolOptions()
                        .setMaxSize(5);

                MySQLPool client = MySQLPool.pool(vertx, dbConnOptions, poolOptions);

                client.getConnection(ar1 -> {
                    if (ar1.succeeded()) {
                        SqlConnection conn = ar1.result();
                        conn.query("select * from block order by num desc limit 20")
                                .execute(ar2 -> {
                                    conn.close();
                                    if (ar2.succeeded()) {
                                        List<Block> result = new ArrayList<>();
                                        ar2.result().forEach(e -> {
                                            Block blk = new Block();
                                            blk.setNumber(e.getLong("num"));
                                            blk.setHash(e.getString("block_hash"));
                                            blk.setParentHash(e.getString("parent_hash"));
                                            System.out.println(e.toString());
                                            result.add(blk);
                                        });

//            vertx.deployVerticle(new BlockCacheVerticle());
                                    } else {
                                        System.out.println("查询失败！原因：" + ar2.cause());
                                    }
                                });
                    } else {
                        System.out.println("获取db链接失败！");
                    }
                });
            } else {

            }
        });

//    HttpClient httpClient = vertx.createHttpClient(new HttpClientOptions()
//            .setConnectTimeout(3000)
//            .setSsl(false)
//            .setKeepAlive(true)
//            .setIdleTimeout(120)
//            .setReusePort(true)
//            .setReuseAddress(true)
//            .setKeepAliveTimeout(120)
//            .setTcpFastOpen(true)
//            .setTcpNoDelay(true)
//            .setTcpQuickAck(true)
//            .setMaxPoolSize(2));
//
//    WebClient webClient = WebClient.wrap(httpClient);


        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Content-Type", "application/json");
        JsonRpcHttpClient jsonRpcHttpClient = new JsonRpcHttpClient(new URL("https://eth.getblock.io/12925c6c-f0ad-41f8-8cce-fec20ed82fed/mainnet/"), headers);

        Future<String> deplySync = vertx.deployVerticle(new SyncVerticle(jsonRpcHttpClient));
        Future<String> deplyBlkCache = vertx.deployVerticle(new BlockCacheVerticle());

        CompositeFuture future = CompositeFuture.join(
                deplySync,
                deplyBlkCache);
        future.onSuccess((f) -> System.out.println("Startup complete!"));

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


}
