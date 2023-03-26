package com.rj.sync.model;

import com.rj.sync.model.original.OriginalLog;

import java.util.List;

public class Receipt {
    // Consensus fields: These fields are defined by the Yellow Paper
    Integer Type;
    Byte postState;
    Long status;
    Long cumulativeGasUsed;
    byte[] bloom;
    List<OriginalLog> logs;

    // Implementation fields: These fields are added by geth when processing a transaction.
    // They are stored in the chain database.
    String txHash;
    String contractAddress;
    Long gasUsed;

    // Inclusion information: These fields provide information about the inclusion of the
    // transaction corresponding to this receipt.
    String blockHash;
    String blockNumber;
    Integer transactionIndex;
}
