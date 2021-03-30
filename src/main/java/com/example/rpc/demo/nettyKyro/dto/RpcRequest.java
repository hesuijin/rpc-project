package com.example.rpc.demo.nettyKyro.dto;

import lombok.*;

/**
 * RPC框架 客户端请求实体
 * @author HSJ
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@ToString
@Data
public class RpcRequest {
    private String interfaceName;
    private String methodName;
}