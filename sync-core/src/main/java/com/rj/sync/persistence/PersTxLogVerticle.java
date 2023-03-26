package com.rj.sync.persistence;

import com.rj.sync.event.codec.PersBlockEvent;
import com.rj.sync.model.Block;
import com.rj.sync.model.Tx;
import com.rj.sync.utils.DbUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.SqlConnection;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PersTxLogVerticle extends AbstractVerticle {

    final MySQLPool pool;

    public PersTxLogVerticle(MySQLPool pool) {
        this.pool = pool;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.eventBus().localConsumer("pers", this::onPers);
    }

    private void onPers(Message<PersBlockEvent> msg) {
        PersBlockEvent evt = msg.body();
        List<Block> reverseBlks = evt.reverseBlks;
        List<Block> saveBlks = evt.saveBlks;
        List<Tx> saveTxs = convert(saveBlks);

        DbUtil.getConn(pool).compose(conn ->
            doDel(conn, reverseBlks)
        ).compose(ar->
            doInsert(ar, saveTxs)
        ).onSuccess(ar -> {
            log.info("区块交易持久化成功，区块: {}, 区块: {}",saveBlks.get(0).number,saveBlks.get(saveBlks.size()-1).number);
        }).onFailure(err -> log.error("",err));
    }

    private Future<SqlConnection> doDel(SqlConnection conn, List<Block> reverseBlks) {
        Promise<SqlConnection> promise = Promise.promise();
        conn.query(buildDelSql(reverseBlks)).execute().onSuccess(ar->promise.complete(conn)).onFailure(er->promise.fail(er));
        return promise.future();
    }

    private String buildDelSql(List<Block> reverseBlks) {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }

    private Future<Integer> doInsert(SqlConnection conn, List<Tx> saveTxs) {
        Promise<Integer> promise = Promise.promise();
        conn.query(buildInsertSql(saveTxs)).execute().onSuccess(ar->promise.complete(1)).onFailure(er->promise.fail(er));
        return promise.future();
    }

    private String buildInsertSql(List<Tx> saveTxs) {
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }

    private List<Tx> convert(List<Block> saveBlks) {
        List<Tx> result = new ArrayList<>();
        for (Block saveBlk : saveBlks) {
            result.addAll(saveBlk.txs);
        }
        return result;
    }
}
