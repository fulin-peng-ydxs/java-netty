package cases.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author PengFuLin
 * @description 处理器初始化器
 * @date 2022/5/24 22:01
 */
public class HttpHandlerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                //加入一个netty 提供的httpServerCodec codec =>[coder - decoder]
                //HttpServerCodec 说明
                //HttpServerCodec 是netty 提供的处理http的 编-解码器
                .addLast(new HttpServerCodec())
                //客户端请求处理器
                .addLast(new HttpServerHandler());
        System.out.println("请求通道的管道处理器初始化完成");
    }
}
