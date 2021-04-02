package com.example.demo.remoting.dto;

import com.example.common.entity.RpcServiceProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@Data
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 1905122041950251207L;
    private String requestId;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;

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
