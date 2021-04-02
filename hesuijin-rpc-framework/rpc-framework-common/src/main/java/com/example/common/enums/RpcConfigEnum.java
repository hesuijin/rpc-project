package com.example.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@AllArgsConstructor
@Getter
public enum RpcConfigEnum {

    /**
     *rpc配置
     */
    RPC_CONFIG_PATH("rpc.properties"),
    /**
     * zookeeper地址
     */
    ZK_ADDRESS("rpc.zookeeper.address");

    private final String propertyValue;
}
