package com.example.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2023/3/19
 */
@AllArgsConstructor
@Getter
@ToString
public enum  RpcResponseCodeEnum {

    //状态码
    SUCCESS(200, "The remote call is successful"),
    FAIL(500, "The remote call is fail");

    private final int code;

    private final String message;
}
