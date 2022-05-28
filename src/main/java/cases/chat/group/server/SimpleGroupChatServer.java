package cases.chat.group.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author PengFuLin
 * @description 群聊服务端
 * @date 2022/5/24 23:52
 */
public class SimpleGroupChatServer {

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup=new NioEventLoopGroup(1);
        EventLoopGroup workGroup=new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //添加String解码器
                        pipeline.addLast("decoder", new StringDecoder());
                        //添加String编码器
                        pipeline.addLast("encoder", new StringEncoder());
                        //添加服务端群聊管理处理器
                        pipeline.addLast(new SimpleServerHandler());

                    }
                });
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(8080)
                    .addListener(future -> {
                        if (future.isSuccess()) {
                            System.out.println("完成对端口8080的监听");
                        }else{
                            System.out.println("未完成对端口8080的监听："+future.cause().getMessage());
                        }
                    });
            channelFuture.channel().closeFuture().addListener(future -> {
                System.out.println("......");
            }).sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            System.out.println("服务器已关闭...");
        }
    }
}
