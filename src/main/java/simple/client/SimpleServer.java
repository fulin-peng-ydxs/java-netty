package simple.client;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import simple.handler.ServerHandler;

import java.util.Collections;
import java.util.List;

/**
 * @author PengFuLin
 * @description 入门的服务端
 * @date 2022/5/22 22:01
 */
public class SimpleServer {

    public static void run(List<Class<? extends ChannelHandler>> handlersClass,ChannelInitializer<SocketChannel>...channelInitializer) throws Exception{
        //创建BossGroup 和 WorkerGroup
        //说明
        //1. 创建两个线程组 bossGroup 和 workerGroup
        //2. bossGroup 只是处理连接请求 , 真正的和客户端业务处理，会交给 workerGroup完成
        //3. 两个都是无限循环
        //4. bossGroup 和 workerGroup 含有的子线程(NioEventLoop)的个数
        //   默认实际 cpu核数 * 2
        EventLoopGroup bossGroup=new NioEventLoopGroup(1);
        EventLoopGroup workGroup=new NioEventLoopGroup();
        //创建服务启动器
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        //配置启动器参数
        ServerBootstrap bootstrap = serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128) //设置服务端连接线程的队列大小
                //.handler() 设置boss组处理器
                .childOption(ChannelOption.SO_KEEPALIVE, true);//设置请求通道一直保持活动连接状态
        if(channelInitializer.length==0){
            bootstrap
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            for (Class<? extends ChannelHandler> aClass : handlersClass) {
                                ch.pipeline().addLast(aClass.newInstance());
                            }
                        }
                    }); //设置work组处理器
        }else{
            bootstrap.childHandler(channelInitializer[0]);
        }
        //启动服务器:绑定服务端口
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(8080)
                    .addListener(future -> { //加入对上个操作bind的监听
                        if (future.isSuccess()) {
                            System.out.println("完成对端口8080的监听");
                        }else{
                            System.out.println("未完成对端口8080的监听："+future.cause().getMessage());
                        }
                    });
            //关闭通道时通知channelFuture，使用同步等待此事件完成：即对通道的关闭进行监听
            // （服务端通道关闭应该是指返回服务端的通道）
            channelFuture.channel().closeFuture().addListener(future -> {
                System.out.println("......");
            }).sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            System.out.println("服务器已关闭...");
        }
    }

    public static void main(String[] args) throws Exception {
        run(Collections.singletonList(ServerHandler.class));
    }
}

