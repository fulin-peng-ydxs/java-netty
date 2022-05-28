package simple.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import simple.handler.ClientHandler;

import java.util.Collections;
import java.util.List;

/**
 * @author PengFuLin
 * @description 入门客户端
 * @date 2022/5/22 20:47
 */
public class SimpleClient {

    public static  void run(List<Class<? extends ChannelHandler>> handlersClass,
                            ChannelInitializer<SocketChannel>...channelInitializer) throws Exception{
        //创建事件循环组：reactor组
        EventLoopGroup clientGroup=new NioEventLoopGroup();
        //创建客户端启动器
        Bootstrap clientBootStrap=new Bootstrap();
        //配置启动参数
        Bootstrap bootstrap = clientBootStrap.group(clientGroup)
                //设置管道类型
                .channel(NioSocketChannel.class);
        if(channelInitializer.length==0){
            bootstrap
                    //设置事件处理器：这里使用ChannelInitializer通道初始化器为通道中的管道添加处理器
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            for (Class<? extends ChannelHandler> aClass : handlersClass) {
                                ch.pipeline().addLast(aClass.newInstance());
                            }
                        }
                    });
        }else{
            bootstrap.handler(channelInitializer[0]);
        }
        try {
            //连接服务器:异步操作，返回通道的未来任务
            ChannelFuture channelFuture = clientBootStrap.connect("localhost", 8080).sync();
            //关闭通道时通知channelFuture，使用同步等待此事件完成：即对通道的关闭进行监听
            channelFuture.channel().closeFuture().addListener(future -> {
                System.out.println("检测到客户端连接通道已关闭，客户端准备开始关闭");
            }).sync();
        }finally {
            //关闭reactor线程组
            clientGroup.shutdownGracefully();
            System.out.println("客户端已关闭..");
        }


    }
    public static void main(String[] args) throws Exception{
        run(Collections.singletonList(ClientHandler.class));
    }
}
