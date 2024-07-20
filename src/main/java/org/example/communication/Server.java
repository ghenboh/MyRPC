package org.example.communication;

import org.example.Service.TestServiceImpl;
import org.example.server.NettyServer;
import org.example.server.RPCServer;
import org.example.transfer.ServiceProvider;

public class Server {
    public static void main(String[] args) {
        TestServiceImpl testService = new TestServiceImpl();
        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 7788);
        serviceProvider.addService(testService);
        RPCServer rpcServer = new NettyServer(serviceProvider);
        rpcServer.start(7788);
    }
}
