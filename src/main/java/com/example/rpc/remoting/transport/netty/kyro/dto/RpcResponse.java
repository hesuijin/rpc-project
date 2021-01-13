package com.example.rpc.remoting.transport.netty.kyro.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@ToString
public class RpcResponse {
   private String message;
}