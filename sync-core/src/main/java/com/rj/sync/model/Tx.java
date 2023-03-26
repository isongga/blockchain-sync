package com.rj.sync.model;

import java.math.BigInteger;


public class Tx {
    public Byte txType;
    public String from;
    public String to;
    public String hash;
    public Integer index;
    public BigInteger value;
    public String input;
    public Long nonce;
    public BigInteger gasPrice;
    public BigInteger gasLimit;
    public BigInteger gasUsed;
    public Boolean isContract;
    public Boolean isContractCreate;
    public Integer blockTime;
    public Integer blockNum;
    public String blockHash;
    public Byte execStatus;
    // Erc20Transfers       []Erc20Transfer
//    EventLogs []*EventLog
//    // Erc20Infos           []*Erc20Info
//    BaseFee              *big.Int
//    MaxFeePerGas         *big.Int //交易费上限
//    MaxPriorityFeePerGas *big.Int //小费上限
//    BurntFees            *big.Int //baseFee*gasused
}
