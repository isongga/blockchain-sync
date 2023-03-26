package com.rj.sync;

public enum DataType {
    BLOCK,
    TX,
    TX_LOG,
    TX_INTERNAL,

    TX_ERC20,
    TX_ERC721,
    TX_ERC1155,

    BALANCE_NATIVE,
    BALANCE_ERC20,
    BALANCE_ERC721,
    BALANCE_ERC1155,

    INFO_ERC20,
    INFO_ERC721,
    INFO_ERC1155;
}
