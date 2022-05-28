package simple;

import io.netty.util.NettyRuntime;
import org.junit.Test;

/**
 * @author PengFuLin
 * @description 简单测试
 * @date 2022/5/23 11:31
 */
public class SimpleTest {

    @Test
    public void test(){
        //获取处理器核心
        System.out.println(NettyRuntime.availableProcessors());
    }
}
