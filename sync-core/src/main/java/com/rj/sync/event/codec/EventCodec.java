package com.rj.sync.event.codec;

import com.rj.sync.event.Event;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class EventCodec implements MessageCodec<Event, Event> {

    public static final EventCodec INSTANCE = new EventCodec();

    public static final String NAME = "EVENT_CODEC";

    @Override
    public void encodeToWire(Buffer buffer, Event model) {
        // for cluster
    }

    @Override
    public Event decodeFromWire(int pos, Buffer buffer) {
        // for cluster
        return null;
    }

    @Override
    public Event transform(Event model) {
        return model;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
