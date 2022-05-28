package core.codec.decoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author PengFuLin
 * @description 解码器适配处理器
 * @date 2022/5/27 0:18
 */
public class SimpleDecoderHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("读取到解码后的消息："+msg);
    }
}
