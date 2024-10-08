package org.example.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.common.RPCRequest;
import org.example.common.RPCResponse;
import org.example.communication.Client;
import org.example.registration.RegisterService;

import javax.security.auth.callback.Callback;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public class NettyClient implements RPCClient {
    private static final Bootstrap bootstrap = new Bootstrap();
    private static final EventLoopGroup group = new NioEventLoopGroup();
    private static final RegisterService registerService = new RegisterService();
    static int timeout = 0;

    private String serverName;

    static {
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new NettyClientInitializer());
    }

    @Override
    public RPCResponse sendRequest(RPCRequest request) {
        try {
            InetSocketAddress inetSocketAddress = registerService.serviceDiscover(serverName + request.getInterfaceName());
            ChannelFuture future = bootstrap.connect(inetSocketAddress.getHostName(), inetSocketAddress.getPort()).sync();
            Channel channel = future.channel();
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
            AttributeKey<RPCResponse> key = AttributeKey.valueOf("GetRPCResponse");
            return channel.attr(key).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void sendRequestAsync(RPCRequest request, Client client, String serviceName) {
        InetSocketAddress inetSocketAddress = registerService.serviceDiscover(serverName + request.getInterfaceName());
        bootstrap.connect(inetSocketAddress.getHostName(), inetSocketAddress.getPort()).addListener((ChannelFuture future) -> {
            Consumer<RPCResponse> callback = client.getConsumer(serviceName);
            if (future.isSuccess()) {
                Channel channel = future.channel();
                channel.writeAndFlush(request);
                channel.closeFuture().addListener((ChannelFuture closeFuture) -> {
                    AttributeKey<RPCResponse> key = AttributeKey.valueOf("GetRPCResponse");
                    RPCResponse response = channel.attr(key).get();
                    client.tryAcquire();
                    callback.accept(response);
                });
            } else {
                callback.accept(RPCResponse.getFailureResponse());
            }
        });
    }
}
