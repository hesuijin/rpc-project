package com.example.api;

import lombok.*;

import java.io.Serializable;

/**
 * @Description: 该类为传输类 需要进行序列化
 * @Author HeSuiJin
 * @Date 2021/4/5
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Hello implements Serializable {
    private String message;
    private String description;
}
