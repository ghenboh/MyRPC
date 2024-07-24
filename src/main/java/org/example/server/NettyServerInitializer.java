package org.example.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import org.example.serialize.Decoder;
import org.example.serialize.Encoder;
import org.example.serialize.KryoSerializer;
import org.example.transfer.ServiceProvider;

@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline channelPipeline = ch.pipeline();
        channelPipeline.addLast(new Decoder());
        channelPipeline.addLast(new Encoder(new KryoSerializer()));
        channelPipeline.addLast(new NettyServerHandler(serviceProvider));
    }
}
