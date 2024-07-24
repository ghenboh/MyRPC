package org.example.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.example.serialize.Decoder;
import org.example.serialize.Encoder;
import org.example.serialize.KryoSerializer;


public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline channelPipeline = ch.pipeline();
        channelPipeline.addLast(new Decoder());
        channelPipeline.addLast(new Encoder(new KryoSerializer()));
        channelPipeline.addLast(new NettyClientHandler());
    }
}
