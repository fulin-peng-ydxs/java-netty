package cases.rpc.service;

//远程本地调用接口
public interface HelloService {

    String protocolName="HelloService#hello#";;

    String hello(String msg);
}
