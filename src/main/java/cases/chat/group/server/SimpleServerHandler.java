package cases.chat.group.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;

/**
 * @author PengFuLin
 * @description 群聊服务端处理器
 * @date 2022/5/24 23:57
 */
public class SimpleServerHandler extends SimpleChannelInboundHandler<String> {


    //public static List<Channel> channels = new ArrayList<Channel>();

    //使用一个hashmap 管理
    //public static Map<String, Channel> channels = new HashMap<String,Channel>();

    //定义一个channle组，管理所有的channel
    //GlobalEventExecutor.INSTANCE 是全局的事件执行器，单例
    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    //时间格式化
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //当请求通道建立后会执行
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将该客户加入聊天的信息推送给其它在线的客户端
        /*
        该方法会将 channelGroup 中所有的channel 遍历，并发送 消息，
        我们不需要自己遍历
         */
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 上线了~" + sdf.format(new java.util.Date()) + " \n");
        //将客户请求通道加入到群聊组中
        channelGroup.add(channel);
    }

    //当请求通道关闭后会执行
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 下线了~\n");
        System.out.println("channelGroup size" + channelGroup.size()); //channelGroup会自动移除关闭的请求通道
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //获取到当前客户的channel
        Channel channel = ctx.channel();
        //遍历channelGroup, 根据不同的情况，回送不同的消息
        channelGroup.forEach(ch -> {
            if(channel != ch) { //不是当前的channel,转发消息
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + " 发送了消息" + msg + "\n");
            }else {//回显客户自己发送的消息给自己
                ch.writeAndFlush("[自己]发送了消息" + msg + "\n");
            }
        });
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
