package cases.rpc.consumer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    //上下文
    private ChannelHandlerContext context;

    //返回的结果
    private String result;

    //客户端调用方法时，传入的参数
    private String para;

    public void setPara(String para) {
        this.para = para;
    }

    //与服务器的连接创建后，就会被调用, 这个方法是第一个被调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("netty客户端正在初始化...");
        context = ctx; //因为我们在其它方法会使用到 ctx
    }

    //收到服务器的数据后，调用方法
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("netty客户端正在收取服务端信息...");
        result = msg.toString();
        notify(); //唤醒等待的线程
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("远程调用服务异常："+cause.getMessage());
        System.out.println("正在关闭客户端...");
        ctx.close();
    }

    //被代理对象调用, 发送数据给服务器，-> wait -> 等待被唤醒(channelRead) -> 返回结果
    @Override
    public synchronized Object call() throws Exception {
        System.out.println("正在远程调用服务..");
        context.writeAndFlush(para);
        //进行wait
        wait(); //等待channelRead 方法获取到服务器的结果后，唤醒
        System.out.println("远程调用服务成功!");
        return  result; //服务方返回的结果

    }
}
