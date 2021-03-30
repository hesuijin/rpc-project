package com.example.demo.builderDemo;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 * @Author HeSuiJin
 * @Date 2021/1/9 15:09
 * @Description:
 */
@Builder(toBuilder = true)
@Getter
@Data
public class MyDTO {

    private String name;

    private String phone;
}
