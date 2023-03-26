package com.rj.sync.persistence;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.HexUtil;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.rj.sync.DataType;
import com.rj.sync.event.codec.AfterPersEvent;
import com.rj.sync.event.codec.PersBlockEvent;
import com.rj.sync.model.Block;
import com.rj.sync.model.original.OriginalBlock;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.collection.LongObjectHashMap;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.SqlConnection;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.util.List;

@Slf4j
public class PersBlockVerticle extends AbstractVerticle {
    final String TB_NAME = "block";
    final MySQLPool pool;

    final IntObjectMap<Integer> persStates  = new IntObjectHashMap<>();

    public PersBlockVerticle(MySQLPool pool) {
        this.pool = pool;
    }


    @Override
    public void start(Promise<Void> startPromise) {

        vertx.eventBus().localConsumer("pers", this::onSave);
        vertx.eventBus().localConsumer("pers.over", this::onReport);
    }

    private void onReport(Message<AfterPersEvent> msg) {
        AfterPersEvent event = msg.body();
        Integer f = persStates.getOrDefault(event.blockNum, 0);
        persStates.put(event.blockNum, f++);

        if(persStates.get(event.blockNum).compareTo(DataType.values().length) == 0) {
            pool.getConnection()
                    .compose(conn -> update(conn, event.blockNum))
                    .onSuccess(i -> log.info("区块持久化完毕！区块号：{}", event.blockNum) )
                    .onFailure(e -> log.error("区块持久状态更新失败，区块号：{}， err:", event.blockNum,e));
        }
    }

    private Future<Integer> update(SqlConnection conn, Integer blockNum) {
        Promise<Integer> promise = Promise.promise();
        conn.query(buildUpdateSql(blockNum)).execute().onSuccess(r -> {
            conn.close();
            promise.complete();
        }).onFailure(h -> promise.fail(h));
        return promise.future();
    }

    private void onSave(Message<PersBlockEvent> msg) {
        PersBlockEvent event = msg.body();
        pool.getConnection()
                // Transaction must use a connection
                .onSuccess(conn -> {
                    // Begin the transaction
                    doPersist(conn, event.reverseBlks, event.saveBlks);
                })
                .onFailure(e -> log.error("获取链接失败：", e));
    }

    private void doPersist(SqlConnection conn, List<Block> revertBlocks, List<Block> saveblocks) {
        conn.begin().compose(tx -> conn.query(buildDelSql(revertBlocks)).execute().compose(res2 -> conn.query(buildInsertSql(saveblocks)).execute())
                        // Commit the transaction
                        .compose(res3 -> tx.commit()))
                // Return the connection to the pool
                .eventually(v -> conn.close())
                .onSuccess(v -> {
                    log.info("区块回滚成功， 开始区块：{}::{}, 结束区块：{}::{}", revertBlocks.get(0).number, revertBlocks.get(0).hash, revertBlocks.get(revertBlocks.size() - 1).number, revertBlocks.get(revertBlocks.size() - 1).hash);
                    log.info("区块持久化成功， 开始区块：{}::{}, 结束区块：{}::{}", saveblocks.get(0).number, saveblocks.get(0).hash, saveblocks.get(saveblocks.size() - 1).number, saveblocks.get(saveblocks.size() - 1).hash);

                })
                .onFailure(err -> {
                    log.info("区块回滚成功， 开始区块：{}::{}, 结束区块：{}::{}", revertBlocks.get(0).number, revertBlocks.get(0).hash, revertBlocks.get(revertBlocks.size() - 1).number, revertBlocks.get(revertBlocks.size() - 1).hash);
                    log.info("区块持久化成功， 开始区块：{}::{}, 结束区块：{}::{}", saveblocks.get(0).number, saveblocks.get(0).hash, saveblocks.get(saveblocks.size() - 1).number, saveblocks.get(saveblocks.size() - 1).hash);
                });
    }

    private String buildUpdateSql(Integer blkNum) {
        StringBuilder sb = new StringBuilder();
        sb.append("update block set pers_state = 1 where num = ").append(blkNum);
        return sb.toString();
    }


    private String buildDelSql(List<Block> blks) {
        if (CollectionUtil.isEmpty(blks)) return "";
        StringBuilder sb = new StringBuilder("delete from ");
        sb.append(TB_NAME);
        sb.append(" where num in (");
        for (Block blk : blks) {
            sb.append(blk.number);
            sb.append(",");
        }
        sb.replace(sb.length() - 1, sb.length() - 1, ")");
        sb.append(";");
        return sb.toString();
    }

    private String buildInsertSql(List<Block> blks) {
        if (CollectionUtil.isEmpty(blks)) return "";
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(TB_NAME);
        sb.append("(num, block_hash, difficulty, miner, parent_hash,block_timestamp) values ");
        for (Block blk : blks) {
            sb.append("(");
            sb.append(blk.number);
            sb.append(blk.hash);
            sb.append(blk.difficulty);
            sb.append(blk.minner);
            sb.append(blk.parentHash);
            sb.append(blk.timestamp);
            sb.append(")");
        }
        sb.append(";");
        return sb.toString();
    }


}
