package com.rj.sync.utils;

import com.rj.sync.event.Event;
import com.rj.sync.event.codec.EventCodec;
import io.vertx.core.*;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.eventbus.impl.EventBusImpl;
import io.vertx.core.eventbus.impl.MessageImpl;

import java.util.*;

public final class EventBusUtils {

    private static final ImmutableMultiMap HEADERS = new ImmutableMultiMap();

    private EventBusUtils() {
    }

//    public static void send(Vertx vertx, String address, Event event) {
//        sendOrPub(vertx, address, event, true, null);
//    }

//    public static void send(Vertx vertx, String address, Event event, ReplyHandler<AsyncResult<Message<Event>>> replyHandler) {
//        sendOrPub(vertx, address, event, true, replyHandler);
//    }

//    public static void publish(Vertx vertx, String address, Event event) {
//        sendOrPub(vertx, address, event, false, null);
//    }

//    @SuppressWarnings("unchecked")
//    private static void sendOrPub(Vertx vertx, String address, Event event, boolean send,
//                                  ReplyHandler<AsyncResult<Message<Event>>> replyHandler) {
//        EventBus eventBus = vertx.eventBus();
//        if (eventBus instanceof EventBusImpl) {
//            EventBusImpl eb = (EventBusImpl) eventBus;
//            eb.sendOrPubInternal(new NoHeadersMessage(address, event, EventCodec.INSTANCE, send, eb), new DeliveryOptions(), replyHandler, null);
//        } else if (send) {
//            eventBus.send(address, event, new DeliveryOptions().setCodecName(EventCodec.NAME));
//        } else {
//            eventBus.publish(address, event, new DeliveryOptions().setCodecName(EventCodec.NAME));
//        }
//    }


    //////// 数据同步相关 *.sync
    public static final String HOURS24_ADDR = "h24.sync"; // 24小时交易量的地址


    //////// 数据推送地址 *.push
    public static final String PUSH_COMMON_ADDR = "common.push"; // 推送普通行情数据
    public static final String PUSH_TRADE_DETAIL_ADDR = "trades.push"; // 推送实时成交明细


    //////// 内部控制命令地址 *.cmd
    public static final String TRADE_CACHE_REQ = "trade.req.cmd"; // 内部查询Trade命令
    public static final String CANDLESTICK_CACHE_RESET = "kline.reset.cmd"; // 内部重置K线命令
    public static final String CANDLESTICK_ZERO_RESET = "kline.zero.reset.cmd";
    public static final String QUERY_24H_VOLUME = "24h.vol.cmd"; // 内部rest查询24小时成交量

    //////// 订阅/取消订阅地址 *.sub
    public static final String SUB_TRADE_DETAILS_ADDR = "trades.sub";// SUB/UNSUB Trade details
    public static String subShardingAddress(int shardings) {
        return shardings + ".sub";
    }


    //////// publish *.pub
    public static final String CLOSE_ADDR = "close.pub"; // 客户端关闭
    public static final String CANDLESTICK_PERSIST_ADDR = "candlestick.persist.pub"; // k线持久化数据
    public static final String TRADE_PERSIST_ADDR = "trade.persist.pub"; // 成交记录持久化数据

    public static final String EXCHANGE_SYMBOL_ADDR = "symbols.pub"; // 交易所的交易对更新地址

    //////// REST 请求 *.rest
    public static final String REST_TRADE_HISTORY_ADDR = "trades.rest"; // Trade history请求

    //////// 回复 *.reply
    public static final String SUMMARY_REPLY_INSTIDS = "summary.instid.reply";


    @SuppressWarnings("unchecked")
    private static class NoHeadersMessage<U, V> extends MessageImpl {

        public NoHeadersMessage(String address, U sentBody, MessageCodec<U, V> messageCodec, boolean send, EventBusImpl bus) {
            super(address, null, sentBody, messageCodec, send, bus);
        }

        protected NoHeadersMessage(NoHeadersMessage<U, V> other) {
            super(other);
        }

        @Override
        public MultiMap headers() {
            return HEADERS;
        }

        @Override
        public MessageImpl<U, V> copyBeforeReceive() {
            return new NoHeadersMessage<>(this);
        }
    }

    @SuppressWarnings("unchecked")
    private static class ImmutableMultiMap implements MultiMap {

        @Override
        public String get(CharSequence name) {
            return null;
        }

        @Override
        public String get(String name) {
            return null;
        }

        @Override
        public List<String> getAll(String name) {
            return Collections.EMPTY_LIST;
        }

        @Override
        public List<String> getAll(CharSequence name) {
            return Collections.EMPTY_LIST;
        }

        @Override
        public List<Map.Entry<String, String>> entries() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public boolean contains(String name) {
            return false;
        }

        @Override
        public boolean contains(CharSequence name) {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Set<String> names() {
            return Collections.EMPTY_SET;
        }

        @Override
        public MultiMap add(String name, String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MultiMap add(CharSequence name, CharSequence value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MultiMap add(String name, Iterable<String> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MultiMap add(CharSequence name, Iterable<CharSequence> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MultiMap addAll(MultiMap map) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MultiMap addAll(Map<String, String> headers) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MultiMap set(String name, String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MultiMap set(CharSequence name, CharSequence value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MultiMap set(String name, Iterable<String> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MultiMap set(CharSequence name, Iterable<CharSequence> values) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MultiMap setAll(MultiMap map) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MultiMap setAll(Map<String, String> headers) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MultiMap remove(String name) {
            return this;
        }

        @Override
        public MultiMap remove(CharSequence name) {
            return this;
        }

        @Override
        public MultiMap clear() {
            return this;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Iterator<Map.Entry<String, String>> iterator() {
            return Collections.emptyIterator();
        }
    }
}
