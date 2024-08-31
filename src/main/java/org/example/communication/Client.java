package org.example.communication;

import org.example.service.TestService;
import org.example.client.NettyClient;
import org.example.client.RPCClient;
import org.example.transfer.RPCProxy;

public class Client {
    public static void main(String[] args) {
        RPCClient rpcClient = new NettyClient("server-0");
        RPCProxy rpcProxy = new RPCProxy(rpcClient);
        TestService testService = rpcProxy.getProxy(TestService.class);
        System.out.println(testService.hello());
    }
}
