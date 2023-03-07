package com.rj.sync;

import ch.qos.logback.classic.Logger;
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
import io.vertx.sqlclient.*;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                .compose(ar -> getConn(ar))
                .compose(con -> getLatestBlocks(con, 20))
                .onSuccess(rows -> {
                    List<Block> result = new ArrayList<>();
                    rows.forEach(e -> {
                        Block blk = new Block();
                        blk.setNumber(e.getLong("num"));
                        blk.setHash(e.getString("block_hash"));
                        blk.setParentHash(e.getString("parent_hash"));
                        System.out.println(e.toString());
                        result.add(blk);
                    });
                }).onFailure(s -> {
                    System.out.println("》》》异常："+s);
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
        future.onSuccess((f) -> log.info("Startup complete!"));

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

    private static Future<SqlConnection> getConn(MySQLPool client) {
        Promise<SqlConnection> promise = Promise.promise();
        client.getConnection(ar -> {
            if (ar.succeeded()) {
                SqlConnection conn = ar.result();
                //关键代码
                promise.complete(conn);
            } else {
                System.out.println("db链接不成功");
                promise.fail(ar.cause());
            }
        });
        //关键代码
        return promise.future();
    }

    private static Future<RowSet<Row>> getLatestBlocks(SqlConnection conn, int limit) {
        Promise<RowSet<Row>> promise = Promise.promise();
        conn.preparedQuery("select * from block order by num desc limit ?")
                .execute(Tuple.of(limit), ar2 -> {
                    conn.close();
                    if (ar2.succeeded()) {
                        promise.complete(ar2.result());
                    } else {
                        promise.fail("查询失败，原因是：" + ar2.cause());
                    }
                });
        return promise.future();
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
                .setMaxSize(5);

        MySQLPool client = MySQLPool.pool(vertx, dbConnOptions, poolOptions);
        promise.complete(client);
        return promise.future();
    }
}
