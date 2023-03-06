package com.rj.sync;

import cn.hutool.core.util.HexUtil;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.rj.sync.model.original.OriginalBlock;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
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

    private final JsonRpcHttpClient webClient;

    private Long blkNum = 100L;

    public SyncVerticle(JsonRpcHttpClient webClient) {
        this.webClient = webClient;
    }


    @Override
    public void start(Promise<Void> startPromise) {
//        try {
//            JsonArray params  = new JsonArray.add("0x"+HexUtil.toHex(1000000)).add, true);
//            OriginalBlock result = webClient.invoke("eth_getBlockByNumber", params.stream().toArray(), OriginalBlock.class);
//            System.out.println(result);
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        } catch (Throwable e) {
//            throw new RuntimeException(e);
//        }

    }

    public void onSyncReq() {
        try {
            JsonArray params  = new JsonArray().add("0x"+HexUtil.toHex(1000000)).add(true);
            OriginalBlock result = webClient.invoke("eth_getBlockByNumber", params.stream().toArray(), OriginalBlock.class);
            System.out.println(result);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }



//    public static void main(String[] args) throws Throwable {
//        Map<String, String> headers = new HashMap<String, String>(1);
//        headers.put("Content-Type", "application/json");
//        JsonRpcHttpClient client = null;
//        JsonArray params  = JsonArray.of("0x"+HexUtil.toHex(1000000), true);
//        client = new JsonRpcHttpClient(new URL("https://eth.getblock.io/12925c6c-f0ad-41f8-8cce-fec20ed82fed/mainnet/"), headers);
//        Object result = client.invoke("eth_getBlockByNumber", params.stream().toArray(), OriginalBlock.class);
//        System.out.println(result);
//    }
}
