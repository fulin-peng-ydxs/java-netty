/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package core.handler.threadpool;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import simple.client.SimpleServer;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.Callable;

public class HandlerServerHandler extends ChannelInboundHandlerAdapter {
    // group 就是充当业务线程池，可以将任务提交到该线程池
    // 这里我们创建了16个线程
    static final EventExecutorGroup group = new DefaultEventExecutorGroup(16);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ThreadPoolServerHandler的线程是=" + Thread.currentThread().getName());
        //将任务提交到 group线程池
        group.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                //接收客户端信息
                ByteBuf buf = (ByteBuf) msg;
                byte[] bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
                String body = new String(bytes, StandardCharsets.UTF_8);
                //休眠10秒
                Thread.sleep(10 * 1000);
                System.out.println("group.submit 的call 线程是=" + Thread.currentThread().getName());
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~(>^ω^<)", CharsetUtil.UTF_8));
                return null;
            }
        });

    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    public static void main(String[] args) throws Exception{
        SimpleServer.run(Collections.singletonList(HandlerServerHandler.class));
    }
}
