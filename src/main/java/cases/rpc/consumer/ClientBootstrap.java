package cases.rpc.consumer;

import cases.rpc.service.HelloService;

public class ClientBootstrap {
    public static void main(String[] args) throws  Exception{
        //创建一个消费者
        NettyClient customer = new NettyClient();
        HelloService service = (HelloService) customer.getBean(HelloService.class, HelloService.protocolName);
        //模拟远程调用
        for (;; ) {
            Thread.sleep(2 * 1000);
            //通过代理对象调用服务提供者的方法(服务)
            String res = service.hello("你好 dubbo~");
            System.out.println("调用的结果 res= " + res);
        }
    }
}
