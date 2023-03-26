package com.rj.sync.event.codec;

import com.rj.sync.event.Event;
import com.rj.sync.model.Block;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class PersBlockEvent implements Event {
    public List<Block> reverseBlks;
    public List<Block> saveBlks;
}
