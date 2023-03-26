package com.rj.sync.model;

import lombok.Data;
import java.math.BigInteger;
import java.util.List;

@Data
public class Block {
    public Long number;
    public String hash;
    public BigInteger difficulty;
    public BigInteger totalDifficulty;
    public String nonce;
    public String extraData;
    public Long gasLimit;
    public Long gasUsed;
    public String minner;
    public String parentHash;
    public String receiptsRoot;
    public String shasUncles;
    public Long size;
    public String stateRoot;
    public Integer txCnt;
    public Integer timestamp;
    public Integer unclesCnt;
    public List<Tx> txs;
    public BigInteger baseFee;
    public BigInteger burntFees;
    public List<Balance> Balances;
    public List<Contract> contracts;
    public List<TxInternal> txInternals;

    //0:normal block 1:revert block
    public Byte state;
}
