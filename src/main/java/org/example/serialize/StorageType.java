package org.example.serialize;

import org.example.common.RPCRequest;
import org.example.common.RPCResponse;

import java.util.HashMap;
import java.util.Map;

public class StorageType {
    private static Map<Class, Integer> storageClass;
    private static Map<Integer, Class> storageCode;

    static {
        storageClass = new HashMap<>(Map.of(RPCRequest.class, 0, RPCResponse.class, 1));
        storageCode = new HashMap<>(Map.of(0, RPCRequest.class, 1, RPCResponse.class));
    }

    static int getTypeNumber(Class clazz) {
        return storageClass.getOrDefault(clazz, -1);
    }

    static Class getTypeClass(int type) {
        return storageCode.getOrDefault(type, null);
    }
}
