package com.example.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/5
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hello {
    private String message;
    private String description;
}
