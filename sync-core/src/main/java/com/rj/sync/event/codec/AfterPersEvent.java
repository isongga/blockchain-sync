package com.rj.sync.event.codec;

import com.rj.sync.DataType;
import com.rj.sync.event.Event;

public class AfterPersEvent implements Event {
    public Integer blockNum;
    public DataType dataType;
}
