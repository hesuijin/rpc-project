package com.example.demo.remotingCenter.dto;

import com.example.common.entity.RpcServiceProperties;
import lombok.*;

import java.io.Serializable;

/**
 * @Description: 客户端请求对象体 包含RpcServiceProperties（接口三要素）
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;

    /**
     * 请求ID 请求唯一标识
     */
    private String requestId;

    //方法信息
    private String methodName;
    //参数类型
    private Class<?>[] paramTypes;
    //具体参数
    private Object[] parameters;


    //接口信息三要素：接口类信息  组信息 版本号信息
    private String interfaceName;
    private String group;
    private String version;


    public RpcServiceProperties toRpcProperties() {
        return RpcServiceProperties.builder().serviceName(this.getInterfaceName())
                .group(this.getGroup())
                .version(this.getVersion())
                .build();
    }

}
