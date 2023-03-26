package com.rj.sync.model;

import cn.hutool.core.util.StrUtil;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.rj.sync.utils.Web3jClient;
import com.rj.sync.utils.Web3jUtil;
import lombok.Data;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.utils.Numeric;

@Data
public class EventLog {
    public  String txHash;
    public  Integer topicCnt;
    public  String topic0;
    public  String topic1;
    public  String topic2;
    public  String topic3;
    public  String data;
    public  Integer index;
    public  String addr;

    public Tx tx;

    public boolean isErc20Tx() {
        return StrUtil.isNotBlank(topic0)
                && topic0.equals("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef")
                && StrUtil.isNotBlank(topic1)
                && StrUtil.isNotBlank(topic2)
                && StrUtil.isNotBlank(topic3);
    }

    public boolean isErc721Tx() {
        return false;
    }

    public TxErc20 extractErc20Tx() {
        if(isErc20Tx()) {
            TxErc20 txErc20 = new TxErc20();
            txErc20.sender = extractAddressFromLogTopic(topic1);
            txErc20.receiver = extractAddressFromLogTopic(topic2);
            txErc20.addr = addr;
            txErc20.blockNum = 1L;
            txErc20.hash = txHash;
            txErc20.blockState = 1;
            txErc20.tokenCnt = Numeric.decodeQuantity(data).toString();
            if (txErc20.tokenCnt.length() > 65) {
                txErc20.tokenCntOrigin =  txErc20.tokenCnt;
                txErc20.tokenCnt = "";
            }
            txErc20.logIndex = index;
            txErc20.blockTime = tx.blockTime;
        }
        return null;
    }

//    public String extractErc20Info(Web3jClient jsonRpcHttpClient) {
//        Object[] objs = new Object[]{"name", "symbol", "decimals", "totalSupply"};
//        Web3jUtil
//    }

    private static String extractAddressFromLogTopic(String topic)  {
        byte[] topic1Bytes = Web3jUtil.hexToByte(topic);
        byte[] addressBytes = new byte[20];
        System.arraycopy(topic1Bytes, 12, addressBytes, 0, 20);
        return Numeric.toHexString(addressBytes);
    }

}
