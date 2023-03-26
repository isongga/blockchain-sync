package com.rj.sync.utils;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.SqlConnection;

public class DbUtil {

    public static Future<SqlConnection> getConn(MySQLPool client) {
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
}
