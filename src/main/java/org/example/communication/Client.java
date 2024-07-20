package org.example.communication;

import org.example.Service.TestService;
import org.example.client.NettyClient;
import org.example.client.RPCClient;
import org.example.transfer.RPCProxy;

public class Client {
    public static void main(String[] args) {
        RPCClient rpcClient = new NettyClient();
        RPCProxy rpcProxy = new RPCProxy(rpcClient);
        TestService testService = rpcProxy.getProxy(TestService.class);
        System.out.println(testService.hello());
    }
}
