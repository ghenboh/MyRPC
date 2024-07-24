package org.example.serialize;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class Decoder extends ByteToMessageDecoder {

    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int type = in.readShort();
        Class fromType = StorageType.getTypeClass(type);
        if (fromType == null) {
            throw new ClassNotFoundException();
        }
        int code = in.readShort();
        Serializer serializer = Serializer.getInstanceByCode(code);
        int length = in.readInt();
        byte[] data = new byte[length];
        in.readBytes(data);
        out.add(serializer.deserialize(data, fromType));
    }
}
