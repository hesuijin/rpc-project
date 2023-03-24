package com.example.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 *  接口三要素：接口信息(接口位置+接口类名) - 接口所在组 -接口版本号
 *  用于作为Zookeeper的node节点的前缀
 * @Author HeSuiJin
 * @Date 2023/3/21
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RpcServiceProperties {

    /**
     * 服务名称  serviceName是通过以下方法获取的
     *  Class<?> serviceRelatedInterface = serviceImplObject.getClass().getInterfaces()[0];
     * 获取该接口的   接口所在包 + 名称 ：com.example.api.HelloService
     * String serviceName = serviceRelatedInterface.getCanonicalName();
     *
     */
    private String serviceName;
    /**
     * 服务所在组
     */
    private String group;
    /**
     * 服务版本号
     */
    private String version;

    public String toRpcServiceName() {
        return this.getServiceName() + "-" +this.getGroup() + "-" + this.getVersion();
    }
}
