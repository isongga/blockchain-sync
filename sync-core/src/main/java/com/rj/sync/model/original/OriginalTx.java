package com.rj.sync.model.original;

import lombok.Data;

@Data
public class OriginalTx {
    private String blockHash;

    private String blockNumber;

    private String from;

    private String gas;

    private String gasPrice;

    private String hash;

    private String input;

    private String nonce;

    private String to;

    private String transactionIndex;

    private String value;

    private String type;

    private String v;

    private String r;

    private String s;
}
