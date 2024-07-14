package org.example.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AllArgsConstructor;
import org.example.common.RPCRequest;
import org.example.common.RPCResponse;
import org.example.transfer.ServiceProvider;

import java.lang.reflect.Method;

@AllArgsConstructor
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private ServiceProvider serviceProvider;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RPCRequest request = (RPCRequest) msg;
        Object service = serviceProvider.getService(request.getInterfaceName());
        if (service == null) {
            ctx.writeAndFlush(RPCResponse.getFailureResponse());
            ctx.close();
            return;
        }
        Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
        Object result = method.invoke(service, request.getParameters());
        ctx.writeAndFlush(RPCResponse.getSuccessResponse(result));
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
