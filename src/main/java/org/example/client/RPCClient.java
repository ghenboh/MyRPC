package org.example.client;

import org.example.common.RPCRequest;
import org.example.common.RPCResponse;

import java.util.function.Consumer;

public interface RPCClient {
    RPCResponse sendRequest(RPCRequest request);

    default void sendRequestAsync(RPCRequest request, Consumer<RPCResponse> callback) {
        sendRequest(request);
    }
}
