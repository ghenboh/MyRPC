package org.example.transfer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.client.RPCClient;
import org.example.common.RPCRequest;
import org.example.communication.Client;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Semaphore;

@RequiredArgsConstructor
public class RPCAsyncProxy implements InvocationHandler {
    @NonNull
    String serviceName;

    @NonNull
    RPCClient rpcClient;

    @Nonnull
    Client client;

    Semaphore semaphore = new Semaphore(1);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            rpcClient.sendRequestAsync(RPCRequest.builder().interfaceName(method.getDeclaringClass().getName())
                    .methodName(method.getName()).parameters(args).build(), client, serviceName + method.getDeclaringClass().getName() + "-" + method.getName());
            makeRelease();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T getProxy(Class<T> clazz) {
        try {
            return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void tryAcquire() throws InterruptedException {
        semaphore.acquire();
    }

    public void makeRelease() throws InterruptedException {
        semaphore.release();
    }
}
