package org.example.client;

import org.example.common.RPCRequest;
import org.example.common.RPCResponse;

public interface RPCClient {
    RPCResponse sendRequest(RPCRequest request);
}
