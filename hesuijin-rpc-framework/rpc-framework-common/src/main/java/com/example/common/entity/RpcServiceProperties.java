package com.example.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RpcServiceProperties {

    /**
     * 服务名称
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
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }
}
