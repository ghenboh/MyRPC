package org.example.transfer;

import lombok.AllArgsConstructor;
import org.example.client.RPCClient;
import org.example.common.RPCRequest;
import org.example.common.RPCResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class RPCProxy implements InvocationHandler {
    RPCClient rpcClient;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            RPCResponse rpcResponse = rpcClient.sendRequest(RPCRequest.builder().interfaceName(method.getDeclaringClass().getName())
                    .methodName(method.getName()).parameters(args).build());
            return rpcResponse.getData();
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
}
