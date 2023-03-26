package com.rj.sync;

import com.rj.sync.jsonrpc.JsonRpcRequest;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.ProxyOptions;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class MainVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        System.setProperty("https.protocols", "TLSv1.2");
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());
    }

    private static final String ETHEREUM_ENDPOINT = "https://eth.getblock.io/12925c6c-f0ad-41f8-8cce-fec20ed82fed/mainnet/"; // Replace with your Ethereum endpoint

    private Web3j web3j;

    HttpClient httpClient;
    WebClient webClient;

    @Override
    public void start() {
//       httpClient = vertx.createHttpClient(new HttpClientOptions()
//                .setConnectTimeout(3000)
//                .setSsl(false)
//                .setKeepAlive(true)
//                .setIdleTimeout(120)
//                .setReusePort(true)
//                .setReuseAddress(true)
//                .setKeepAliveTimeout(120)
//                .setTcpFastOpen(true)
//                .setTcpNoDelay(true)
//                .setTcpQuickAck(true)
//                .setMaxPoolSize(2));

//        ProxyOptions op = new ProxyOptions();
//        op.setHost("127.0.0.1");
//        op.setPort()
        WebClientOptions options = new WebClientOptions()
//                .setProxyOptions()
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
                .setMaxPoolSize(2);
//                .setUserAgent("My-App/1.2.3");

        webClient = WebClient.create(vertx, options);
//        Thread thread = new Thread(() -> syncBlockData(100_0000L));
//        thread.start();

        syncBlockData(100_0000L);


    }

    private CompletableFuture<String> sendJsonRpcRequest(JsonRpcRequest jsonRpcRequest) {
        CompletableFuture<String> future = new CompletableFuture<>();
        JsonObject obj = JsonObject.mapFrom(jsonRpcRequest);

        future.runAsync(()->webClient.postAbs("https://eth.getblock.io/12925c6c-f0ad-41f8-8cce-fec20ed82fed/mainnet/")
                .sendJson(obj, ar -> {
                    if (ar.succeeded()) {
                        future.complete(ar.result().bodyAsString());
                    } else {
                        future.completeExceptionally(ar.cause());
                    }
                }));



        return future;
    }


    private void syncBlockData(Long timerId) {
        List<Object> li = new ArrayList<>();
        li.add("0xf4240");
        li.add(true);
        JsonRpcRequest jsonRpcRequest = new JsonRpcRequest("1", "eth_getBlockByNumber", li);
        JsonObject obj = JsonObject.mapFrom(jsonRpcRequest);

        System.out.println("123");

//        CompletableFuture<String> future = sendJsonRpcRequest(jsonRpcRequest);
//
//        future.whenComplete((result, throwable) -> {
//            if (throwable != null) {
//                System.out.println("Got exception " + throwable.getMessage());
//            } else {
//                System.out.println("Got data " + result);
//            }
//        });

//        httpClient.request(HttpMethod.POST, ETHEREUM_ENDPOINT, req -> {
//            if(req.succeeded()) {
//                req.s
//            }
//        });


        webClient.postAbs("https://eth.getblock.io/12925c6c-f0ad-41f8-8cce-fec20ed82fed/mainnet/")
                .sendJson(obj ,ar -> {
                    System.out.println("回调");
                    if(ar.succeeded())
                        System.out.println("Got data " + ar.result().bodyAsString());
                    else
                        System.out.println(ar.cause());
                });

        System.out.println("==-=-");

//        log.info("{}", Thread.currentThread().getName());
//        web3j = Web3j.build(new HttpService(ETHEREUM_ENDPOINT));
//        web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true)
//                .sendAsync()
//                .thenAccept(this::processBlockData)
//                .exceptionally(throwable -> {
//                    log.info("{}", Thread.currentThread().getName());
//                    System.err.println("Failed to sync block data: " + throwable.getMessage());
//                    return null;
//                });
//        System.out.println("============");
    }

    private void processBlockData(EthBlock ethBlock) {
        log.info("processBlockData: {}", Thread.currentThread().getName());
        EthBlock.Block block = ethBlock.getBlock();
        JsonObject blockData = new JsonObject();
        blockData.put("number", block.getNumber().intValue());
        blockData.put("hash", block.getHash());
        blockData.put("timestamp", block.getTimestamp().intValue());
        // Add more block data here as needed
        log.info("bl: {}, blk number:{}", Json.encodePrettily(block), block.getNumber().intValue());
        vertx.eventBus().publish("ethereum.blockData", blockData);
    }

}
