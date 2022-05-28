package core.codec.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @author PengFuLin
 * @description 入门重放解码器
 * @date 2022/5/27 1:08
 */
public class SimpleReplaying extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //在 ReplayingDecoder不需要判断数据是否足够读取
        out.add(in.readLong());
    }
}



