package com.example.rpc.remoting.transport.netty.kyro.dto;

import lombok.*;

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