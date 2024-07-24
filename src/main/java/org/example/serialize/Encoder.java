package org.example.serialize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Encoder extends MessageToByteEncoder {
    private Serializer serializer;

    public void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        int num = StorageType.getTypeNumber(msg.getClass());
        if(num < 0) {
            throw new ClassNotFoundException();
        }
        out.writeShort(num);
        out.writeShort(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }

}
