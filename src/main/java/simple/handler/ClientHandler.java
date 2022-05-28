package simple.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author PengFuLin
 * @description 客户端处理器
 * @date 2022/5/22 21:05
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {


    //通道准备完成
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端已就绪..");
        ctx.writeAndFlush(Unpooled.copiedBuffer("你好，服务器", CharsetUtil.UTF_8));
//        ctx.writeAndFlush(1);
    }

    //通道读取事件
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("接收到服务器返回的消息："+byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("服务器的地址是："+ctx.channel().remoteAddress());
    }

    //运行异常时
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("客户端出现异常："+cause.getMessage());
        //关闭通道
        ctx.close();
    }
}
