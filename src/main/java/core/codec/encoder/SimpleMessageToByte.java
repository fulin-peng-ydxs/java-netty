package core.codec.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import simple.client.SimpleClient;
import simple.handler.ClientHandler;

import java.util.Arrays;

/**
 * @author PengFuLin
 * @description 入门编码器
 * @date 2022/5/27 1:34
 */

public class SimpleMessageToByte extends MessageToByteEncoder<Integer> {

    //为发送指定类型：Integer类型的数据进行编码工作
    //如果不是，则不会进入此编码器
    @Override
    protected void encode(ChannelHandlerContext ctx, Integer msg, ByteBuf out) throws Exception {
        System.out.println("进行int类型数据编码");
        out.writeInt(msg);
    }

    public static void main(String[] args) throws Exception {
        SimpleClient.run(Arrays.asList(SimpleMessageToByte.class,ClientHandler.class));
    }

}
