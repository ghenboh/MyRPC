package org.example.client;

import org.example.common.RPCRequest;
import org.example.common.RPCResponse;
import org.example.communication.Client;

import java.util.function.Consumer;

public interface RPCClient {
    RPCResponse sendRequest(RPCRequest request);

    default void sendRequestAsync(RPCRequest request, Client client, String serviceName) {
        sendRequest(request);
    }
}
