package com.example.api;

import lombok.*;

import java.io.Serializable;

/**
 * @Description: 该类为传输类 需要进行序列化
 * @Author HeSuiJin
 * @Date 2021/4/5
 */
@Data
@AllArgsConstructor
public class Hello implements Serializable {
    /**
     * 信息
     */
    private String message;
    /**
     * 描述
     */
    private String description;
}
