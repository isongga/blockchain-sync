package com.rj.sync;

import cn.hutool.core.util.HexUtil;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.rj.sync.model.original.OriginalBlock;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import org.web3j.protocol.Web3j;

import java.net.MalformedURLException;

/**
 * 定时向节点请求区块数据。
 */

public class SyncNewestBlockVerticle extends AbstractVerticle {

    private final JsonRpcHttpClient jsonRpcHttpClient;

    private Long blockNum; //当前block num

    public SyncNewestBlockVerticle(JsonRpcHttpClient jsonRpcHttpClient) {
        this.jsonRpcHttpClient = jsonRpcHttpClient;
    }


    @Override
    public void start(Promise<Void> startPromise) {
        vertx.eventBus().localConsumer("sync.first", this::setupFirst);
        vertx.setPeriodic(5000, b -> syncAndPublish(blockNum + 1));
    }

    public void setupFirst(Message<Long> blkNum) {
        try {
            blockNum = blkNum.body();
            JsonArray params = new JsonArray().add("0x" + HexUtil.toHex(blockNum)).add(true);
            OriginalBlock result = jsonRpcHttpClient.invoke("eth_getBlockByNumber", params.stream().toArray(), OriginalBlock.class);
            System.out.println(result);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public OriginalBlock syncAndPublish(Long blockNum) {
        JsonArray params = new JsonArray().add("0x" + HexUtil.toHex(blockNum)).add(true);
        try {
            OriginalBlock block = jsonRpcHttpClient.invoke("eth_getBlockByNumber", params.stream().toArray(), OriginalBlock.class);
            publish(block);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public void publish(OriginalBlock block) {
        vertx.eventBus().publish("ca.new", block);
        blockNum = Long.valueOf(block.getNumber());
    }
}
