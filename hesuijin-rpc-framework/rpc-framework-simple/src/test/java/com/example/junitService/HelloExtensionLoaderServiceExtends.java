package com.example.junitService;

import com.example.common.extension.SPI;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2023/3/15
 */
@SPI
@Slf4j
public class HelloExtensionLoaderServiceExtends extends HelloExtensionLoaderServiceParent{

    public void helloExtensionLoaderMethod() {
        log.info("你好啊！ HelloExtensionLoaderServiceExtends 的 HelloExtensionLoaderMethod ");
    }

    public void helloExtensionLoaderMethodOther() {
        log.info("你好啊！ HelloExtensionLoaderServiceExtends 的 HelloExtensionLoaderMethodOther ");
    }

}
