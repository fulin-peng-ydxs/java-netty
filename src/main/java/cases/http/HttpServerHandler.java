package cases.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.nio.charset.Charset;

/**
 * @author PengFuLin
 * @description http处理器
 * @date 2022/5/24 15:06
 */


//说明
//1. SimpleChannelInboundHandler 是 ChannelInboundHandlerAdapter
//   用于接收特定类型的消息
//2. HttpObject 客户端和服务器端相互通讯的数据被封装成 HttpObject
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        //根据http协议要求，请求是状态的，请求结束后，连接通道就会关闭，所以每次请求都会生成新的通道和
        //相关管道和对应的组件
        System.out.println("channel=" + ctx.channel() + " pipeline=" + ctx.pipeline());
        System.out.println("当前ctx的handler=" + ctx.handler());
         //是否为http的请求消息
        if(msg instanceof HttpRequest){
            System.out.println("请求消息类型为："+msg.getClass());
            System.out.println("请求客户端地址为："+ctx.channel().remoteAddress());
            HttpRequest httpRequest = (HttpRequest) msg;
            String url = httpRequest.uri();
            URI uri = new URI(url);  //用于处理请求路径的解码操作
            String path = uri.getPath();
            System.out.println("请求路径："+path);
            if("/favicon.ico".equals(path)){
                return;  //拦截请求，不予处理
            }
            //响应消息
            ByteBuf byteBuf = Unpooled.copiedBuffer("你好，我是服务端，我叫彭帅疯", Charset.forName("gbk"));  //浏览器默认为gbk
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK
                    , byteBuf);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            //数据出站
            ctx.writeAndFlush(response);
        }
    }
}
