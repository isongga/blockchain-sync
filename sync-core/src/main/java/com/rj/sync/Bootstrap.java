package com.rj.sync;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.rj.sync.event.codec.EventCodec;
import io.vertx.core.*;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.ext.web.client.WebClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
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

    WebClient webClient = WebClient.wrap(httpClient);

    Map<String, String> headers = new HashMap<String, String>(1);
    headers.put("Content-Type", "application/json");
    JsonRpcHttpClient jsonRpcHttpClient = new JsonRpcHttpClient(new URL("https://eth.getblock.io/12925c6c-f0ad-41f8-8cce-fec20ed82fed/mainnet/"), headers);

//    vertx.deployVerticle(new SyncVerticle(jsonRpcHttpClient));

//    CompositeFuture future = CompositeFuture.join(
//            jsonRpcProcessorFuture,
//            methodRegistryFuture,
//            httpServerFuture,
//            websocketServerFuture);
//    future.onSuccess((f) -> LOG.info("Startup complete!"));

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
