package com.example.junitService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2023/3/15
 */
@Slf4j
public class HelloExtensionLoaderServiceImpl2 implements HelloExtensionLoaderService{

    @Override
    public void HelloExtensionLoaderMethod(){
        log.info("你好啊！ HelloExtensionLoaderMethod1 ");
    }
}
