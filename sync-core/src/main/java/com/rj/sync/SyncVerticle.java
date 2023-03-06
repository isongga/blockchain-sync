package com.rj.sync;

import cn.hutool.core.util.HexUtil;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.rj.sync.model.original.OriginalBlock;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 定时向节点请求区块数据。
 */

public class SyncVerticle  extends AbstractVerticle {

    private final JsonRpcHttpClient jsonRpcHttpClient;

    public SyncVerticle(JsonRpcHttpClient jsonRpcHttpClient) {
        this.jsonRpcHttpClient = jsonRpcHttpClient;
    }


    @Override
    public void start(Promise<Void> startPromise) {
        vertx.eventBus().localConsumer("nd.req.syc", this::onSyncReq);
    }

    public void onSyncReq(Message<Long> blkNum) {
        try {
            JsonArray params  = new JsonArray().add("0x"+HexUtil.toHex(blkNum.body())).add(true);
            OriginalBlock result = jsonRpcHttpClient.invoke("eth_getBlockByNumber", params.stream().toArray(), OriginalBlock.class);
            System.out.println(result);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
