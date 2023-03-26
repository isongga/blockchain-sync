package com.rj.sync.utils;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.rj.sync.model.EthAddress;
import com.rj.sync.model.original.OriginalBlock;
import io.vertx.core.json.JsonArray;
import org.web3j.utils.Numeric;

import java.lang.reflect.Type;
import java.net.MalformedURLException;

public class Web3jUtil {
    public static boolean has0xPrefix(String str) {
        return str.length() >= 2 && str.charAt(0) == '0' && (str.charAt(1) == 'x' || str.charAt(1) == 'X');
    }

    public static Object rpc(JsonRpcHttpClient jsonRpcHttpClient, String method, Object[] params, Type clazz) {
        try {
            return jsonRpcHttpClient.invoke(method, params, clazz);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] hexToByte(String hexStr) {
        if(has0xPrefix(hexStr)) {
            hexStr = hexStr.substring(2, hexStr.length());
        }
        if (hexStr.length() % 2 == 1) {
            hexStr = "0" + hexStr;
        }
        return Numeric.hexStringToByteArray(hexStr);


    }

//    public static String byte2Address(byte[] data) {
//        byte[] result = new byte[EthAddress.ADDRESS_LENGTH];
//        int index = 0;
//        if(data.length > EthAddress.ADDRESS_LENGTH ){
//            index = data.length-EthAddress.ADDRESS_LENGTH;
//        }
//        copy(a[AddressLength-len(b):], b)
//    }
}
