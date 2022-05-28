package core.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * @author PengFuLin
 * @description Unpooled.copiedBuffer()演示
 * @date 2022/5/24 12:08
 */
public class CopiedBuffer {
    public static void main(String[] args) {
        //创建一个缓冲区，其内容为以指定字符集编码的指定字符串。
        //缓冲区的readerIndex和writerIndex分别为0和编码字符串的长度。
        ByteBuf byteBuf = Unpooled.copiedBuffer("hi，你好，我是彭帅疯哈", StandardCharsets.UTF_8);
        //转成字符串
        byte[] array= new byte[byteBuf.readableBytes()];
        //如果字符的编码长度不等于创建的缓冲区大小时，需要获取实际可用的字节数组，
        //否则可能会出现乱码
        byteBuf.getBytes(   0,array);
        String content = new String(array, StandardCharsets.UTF_8);
        System.out.println("解析字符串为:"+content);
        //相关属性
        System.out.println(byteBuf.readerIndex());  //读索引
        System.out.println(byteBuf.writerIndex()); //写索引
        System.out.println(byteBuf.capacity()); //缓冲区字节大小
        System.out.println(byteBuf.readableBytes()); //目前可读字节
        //按照范围读取
        System.out.println(byteBuf.getCharSequence(0,4,StandardCharsets.UTF_8));
        System.out.println(byteBuf.getCharSequence(4,6,StandardCharsets.UTF_8));
    }
}
