package org.example.communication;

import org.example.common.RPCResponse;
import org.example.service.TestService;
import org.example.client.NettyClient;
import org.example.client.RPCClient;
import org.example.transfer.RPCAsyncProxy;
import org.example.transfer.RPCProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

public class Client {
    Semaphore semaphore = new Semaphore(0);
    Map<String, Consumer<RPCResponse>> storage = new HashMap<>();

    public static void main(String[] args) throws InterruptedException, NoSuchMethodException {
        useService();
        Client client = new Client();
        RPCAsyncProxy rpcAsyncProxy = new RPCAsyncProxy("server-0-", new NettyClient("server-0-"), client);
        TestService testService = rpcAsyncProxy.getProxy(TestService.class);
        client.setConsumer("server-0-" + TestService.class.getName() + "-" + TestService.class.getMethod("hello").getName(), System.out::println);
        testService.hello();
        for (int helper = 0; helper < 10000; helper++) {
            System.out.println(helper); //do some other work
        }
        client.makeRelease();
        rpcAsyncProxy.makeAcquire();
    }

    public void setConsumer(String service, Consumer<RPCResponse> consumer) {
        storage.put(service, consumer);
    }

    public Consumer<RPCResponse> getConsumer(String service) {
        return storage.get(service);
    }

    public static void useService() {
        RPCClient rpcClient = new NettyClient("server-0-");
        RPCProxy rpcProxy = new RPCProxy(rpcClient);
        TestService testService = rpcProxy.getProxy(TestService.class);
        System.out.println(testService.hello());
    }

    public void makeRelease() {
        semaphore.release();
    }

    public void tryAcquire() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
