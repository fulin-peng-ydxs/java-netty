package core.handler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author PengFuLin
 * @description 入门闲置状态检测处理器
 * @date 2022/5/25 22:23
 */
public class SimpleIdleStateHandler {

    static class  SimpleIdleHandlerDemo extends ChannelInboundHandlerAdapter {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                //将  evt 向下转型 IdleStateEvent
                IdleStateEvent event = (IdleStateEvent) evt;
                String eventType = null;
                switch (event.state()) {
                    case READER_IDLE:
                        eventType = "读空闲";
                        break;
                    case WRITER_IDLE:
                        eventType = "写空闲";
                        break;
                    case ALL_IDLE:
                        eventType = "读写空闲";
                        break;
                }
                System.out.println(ctx.channel().remoteAddress() + "-" + eventType);
                System.out.println("服务器做相应处理..");
                //如果发生空闲，可以关闭通道
                 ctx.channel().close();
            }
        }
    }

    public static void main(String[] args) throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    //加入一个netty 提供 IdleStateHandler
                    /*
                    说明
                    1. IdleStateHandler 是netty 提供的处理空闲状态的处理器
                    2. long readerIdleTime : 表示多长时间没有读,
                    3. long writerIdleTime : 表示多长时间没有写,
                    4. long allIdleTime : 表示多长时间没有读写,
                    当 IdleStateEvent 触发后 , 就会传递给管道 的下一个handler去处理通过调用(触发)下一个handler 的 userEventTriggered,
                    在该方法中去处理 IdleStateEvent(读空闲，写空闲，读写空闲)
                     */
                    pipeline.addLast(new IdleStateHandler(7000,7000,10, TimeUnit.SECONDS));
                    //加入一个对空闲检测进一步处理的handler(自定义)
                    pipeline.addLast(new SimpleIdleHandlerDemo());
                }
            });
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
