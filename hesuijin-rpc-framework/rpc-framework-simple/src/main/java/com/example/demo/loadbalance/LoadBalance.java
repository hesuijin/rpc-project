package com.example.demo.loadbalance;

import com.example.common.extension.SPI;

import java.util.List;

//1：接口类LoadBalance的方法核心方法为selectServiceAddress。（负载均衡中  根据指定策略  在服务地址集合中选择其中一个服务地址）
//2：通过抽象类 AbstractLoadBalance来实现LoadBalance的selectServiceAddress，在AbstractLoadBalance中的selectServiceAddress又依赖于doSelect（抽象类中未被实现的方法）。
//3：通过子类（具体实现类如：RandomLoadBalance ConsistentHashLoadBalance）继承AbstractLoadBalance并且重写其doSelect,而doSelect里面就有具体的负载均衡策略。

/**
 * @Description:
 * 负载均衡 获取ServiceAddress策略
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@SPI
public interface LoadBalance {

    String selectServiceAddress(List<String> serviceAddresses, String rpcServiceName);
}
