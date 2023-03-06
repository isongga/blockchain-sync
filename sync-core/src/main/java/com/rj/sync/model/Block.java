package com.rj.sync.model;

import lombok.Data;
import java.math.BigInteger;
import java.util.List;

@Data
public class Block {
    private Long number;
    private String hash;
    private BigInteger difficulty;
    private BigInteger totalDifficulty;
    private String nonce;
    private String extraData;
    private Long gasLimit;
    private Long gasUsed;
    private String minner;
    private String parentHash;
    private String receiptsRoot;
    private String shasUncles;
    private Long size;
    private String stateRoot;
    private Integer txCnt;
    private Integer timestamp;
    private Integer unclesCnt;
    private List<Tx> txs;
    private BigInteger baseFee;
    private BigInteger burntFees;
    private List<Balance> Balances;
    private List<Contract> contracts;
    private List<TxInternal> txInternals;

    //0:normal block 1:revert block
    private Byte state;
}
