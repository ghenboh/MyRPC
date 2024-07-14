package org.example.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;
import org.example.common.RPCRequest;
import org.example.common.RPCResponse;

@AllArgsConstructor
public class NettyClient implements RPCClient{
    private final String host;
    private final int port;
    private static final Bootstrap bootstrap = new Bootstrap();
    private static final EventLoopGroup group = new NioEventLoopGroup();

    static {
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new NettyClientInitializer());
    }

    @Override
    public RPCResponse sendRequest(RPCRequest request) {
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
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
}
