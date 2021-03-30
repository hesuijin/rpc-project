package com.example.demo.nettyKyro.dto;

import lombok.*;

/**
 * RPC框架 服务端相应实体
 * @author HSJ
 */
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@ToString
public class RpcResponse {
   private String message;
}