package cases.sticktcp.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import simple.client.SimpleServer;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;

public class MyServerHandler extends SimpleChannelInboundHandler<ByteBuf>{

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] buffer = new byte[msg.readableBytes()];
        msg.readBytes(buffer);
        //将buffer转成字符串
        String message = new String(buffer, StandardCharsets.UTF_8);
        System.out.println("服务器接收到数据 " + message);
        //服务器回送数据给客户端, 回送一个随机id ,
        ByteBuf responseByteBuf = Unpooled.copiedBuffer(UUID.randomUUID()+ " ", StandardCharsets.UTF_8);
        ctx.writeAndFlush(responseByteBuf);
    }

    public static void main(String[] args) throws Exception {
        SimpleServer.run(Collections.singletonList(MyServerHandler.class));
    }
}
