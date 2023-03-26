package com.rj.sync;

import cn.hutool.core.util.HexUtil;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.rj.sync.model.Block;
import com.rj.sync.model.original.OriginalBlock;
import com.rj.sync.utils.DbUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BlockCacheVerticle extends AbstractVerticle {
    private List<Block> blkCache = new ArrayList<>(20);
    private boolean isReverting = false;
    int INIT_BLK_NUM = 20;
    final MySQLPool pool;

    public BlockCacheVerticle(MySQLPool pool) {
        this.pool = pool;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        DbUtil.getConn(pool).compose(conn->
            getLatestBlocks(conn, INIT_BLK_NUM)
        ).onSuccess(rows -> {
            rows.forEach(e -> {
                Block blk = new Block();
                blk.setNumber(e.getLong("num"));
                blk.setHash(e.getString("block_hash"));
                blk.setParentHash(e.getString("parent_hash"));
                blkCache.add(blk);
            });

            vertx.eventBus().publish("sync.init", blkCache.get(blkCache.size()-1));

            startPromise.complete();
        }).onFailure(s -> {
            log.error("init block error:", s);
            startPromise.fail(s);
        });

        vertx.eventBus().localConsumer("ca.new", this::onReceiveNewBlock);
        vertx.eventBus().localConsumer("ca.rev", this::onReverseBlocks);
    }

    private void onReverseBlocks(Message<List<Block>> msg) {
        //回滚内存数据
        List<Block> blks = msg.body();

        //最后
        vertx.eventBus().publish("pers", blks);
        isReverting = false;
    }

    // 收到新的block之后，更新缓存
    private void onReceiveNewBlock(Message<Block> msg) {
        Block blk = msg.body();
        blk.getParentHash();

        Block latestCache = blkCache.get(blkCache.size()-1);
        if(blk.getParentHash().equals(latestCache.getParentHash())) {
            blkCache.add(blk);
        } else {
            isReverting = true;
            vertx.eventBus().publish("sync.bl", blk);
        }
    }





    private Future<RowSet<Row>> getLatestBlocks(SqlConnection conn, int limit) {
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


}
