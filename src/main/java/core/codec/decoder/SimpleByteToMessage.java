package core.codec.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import simple.client.SimpleServer;

import java.util.Arrays;
import java.util.List;

/**
 * @author PengFuLin
 * @description 入门的解码器
 * @date 2022/5/27 0:01
 */
public class SimpleByteToMessage extends ByteToMessageDecoder {

    /**
     * 解码器：在读取数据时进行解码
     * 1.其方法会被调用多次，将解码的数据添加到out对象后，其类容会被转发到后面的业务处理器进行处理
     * 2.当in被读取完毕时或out为空（这里需要注意：如果不满足数据指字节大小，就不要去读，让下次数据发过来后再重新读取），
     *   其方法的循环调用才会结束。
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        out.add(new String(bytes, CharsetUtil.UTF_8));
    }

    public static void main(String[] args) throws Exception {
        SimpleServer.run(Arrays.asList(SimpleByteToMessage.class,SimpleDecoderHandler.class));
    }
}
