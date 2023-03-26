package com.rj.sync.model;

public class EthAddress {
    // HashLength is the expected length of the hash
    public static final int HASH_LENGTH = 32;
    // AddressLength is the expected length of the address
    public static final int ADDRESS_LENGTH = 20;
    public final String addr;


    public EthAddress(String addr) {
        this.addr = addr;
    }
}
