package com.rj.sync;

import cn.hutool.core.util.HexUtil;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.rj.sync.model.Block;
import com.rj.sync.model.original.OriginalBlock;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class BlockCacheVerticle extends AbstractVerticle {
    private List<Block> blkCache = new ArrayList<>();
    private Boolean isReverting = false;

    public BlockCacheVerticle(JsonRpcHttpClient webClient) {

    }


    @Override
    public void start(Promise<Void> startPromise) {

    }


}
