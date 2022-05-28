package cases.rpc.consumer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//用于创建本地远程调用服务对象
public class NettyClient {
    //创建线程池
    private static ExecutorService executor = Executors.newFixedThreadPool
            (Runtime.getRuntime().availableProcessors());

    private static NettyClientHandler clientHandler ;

    private int count = 0;

    //本地远程调用服务对象使用代理模式，获取其一个代理对象
    public Object getBean(final Class<?> serivceClass, final String providerName) {
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(), //类加载器

                new Class<?>[]{serivceClass}, //要代理的接口

                (proxy, method, args) -> {  //代理处理方法
                    if (clientHandler == null) {
                        initClient();
                    }
                    System.out.println("开始调用远程服务" + (++count) + " 次");
                    //设置要发给服务器端的信息
                    clientHandler.setPara(providerName+args[0]);
                    //将执行提交到线程池中处理，防止多线程调用阻塞
                    return executor.submit(clientHandler).get();

                }
        );
    }

    //使用netty作为本地远程调用实现
    private static void initClient() {
        clientHandler=new NettyClientHandler();
        //创建EventLoopGroup
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new StringDecoder());
                                pipeline.addLast(new StringEncoder());
                                pipeline.addLast(clientHandler);
                            }
                        }
                );

        try {
            bootstrap.connect("127.0.0.1", 7000).addListener(future -> {
                boolean success = future.isSuccess();
                if (success){
                    System.out.println("已建立远程连接..");
                    System.out.println("消费方已做好准备");
                }
                else {
                    System.out.println("未建立远程连接..");
                }
            }).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
