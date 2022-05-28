package cases.sticktcp.simple;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyMessageEncoder extends MessageToByteEncoder<MessageProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) throws Exception {
        System.out.println("MyMessageEncoder.encode()方法被调用");
        out.writeInt(msg.getLen()); //编码约定第一次写入的数据为int类型数据，代表这次请求的业务数据实际的长度
        out.writeBytes(msg.getContent()); //后面的数据为真实的业务数据，数据的长度为第一次读取的到的数据值
    }
}
