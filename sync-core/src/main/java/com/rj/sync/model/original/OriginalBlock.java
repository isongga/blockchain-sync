package com.rj.sync.model.original;

import com.rj.sync.model.Block;
import lombok.Data;

import java.util.List;
/**
 * 对应go版本中的BlockJson
 * eg.
 * {
 *     "jsonrpc": "2.0",
 *     "id": 2,
 *     "result": {
 *         "difficulty": "0x4ea3f27bc",
 *         "extraData": "0x476574682f4c5649562f76312e302e302f6c696e75782f676f312e342e32",
 *         "gasLimit": "0x1388",
 *         "gasUsed": "0x0",
 *         "hash": "0xdc0818cf78f21a8e70579cb46a43643f78291264dda342ae31049421c82d21ae",
 *         "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
 *         "miner": "0xbb7b8287f3f0a933474a79eae42cbca977791171",
 *         "mixHash": "0x4fffe9ae21f1c9e15207b1f472d5bbdd68c9595d461666602f2be20daf5e7843",
 *         "nonce": "0x689056015818adbe",
 *         "number": "0x1b4",
 *         "parentHash": "0xe99e022112df268087ea7eafaf4790497fd21dbeeb6bd7a1721df161a6657a54",
 *         "receiptsRoot": "0x56e81f171bcc55a6ff8345e692c0f86e5b48e01b996cadc001622fb5e363b421",
 *         "sha3Uncles": "0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347",
 *         "size": "0x220",
 *         "stateRoot": "0xddc8b0234c2e0cad087c8b389aa7ef01f7d79b2570bccb77ce48648aa61c904d",
 *         "timestamp": "0x55ba467c",
 *         "totalDifficulty": "0x78ed983323d",
 *         "transactions": [],
 *         "transactionsRoot": "0x56e81f171bcc55a6ff8345e692c0f86e5b48e01b996cadc001622fb5e363b421",
 *         "uncles": []
 *     }
 * }
 */
@Data
public class OriginalBlock {
    private String difficulty;
    private String extraData;
    private String gasLimit;
    private String gasUsed;
    private String miner;
    private String logsBloom;
    private String mixHash;
    private String number;
    private String hash;
    private String totalDifficulty;
    private String nonce;
    private String parentHash;
    private String receiptsRoot;
    private String sha3Uncles;
    private String size;
    private String stateRoot;
    private String timestamp;
    private List<OriginalTx> transactions;
    private String transactionsRoot;
    private List<Block> uncles;

    //记录获取到区块的时间
    private final long createdTime = System.currentTimeMillis();
}
