package com.example.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@Slf4j
public class PropertiesFileUtil {
    private PropertiesFileUtil() {
    }

//    ClassLoader.getSystemClassLoader（） 报错
//    Thread.currentThread().getContextClassLoader() 不会报错。
//    查询资料后解释如下：
//    ClassLoader.getSystemClassLoader方法无论何时均会返回ApplicationClassLoader,其只加载classpath下的class文件。
//    在javaSE环境下，一般javaSE项目的classpath为bin/目录，因此只要编译后的class文件在classpath下就可以。此时ApplicationClassLoader就可以加载动态生成的类。
//    在javaEE环境下，我们的项目里的类是通过WebAppClassLoader类来加载的，此时我们获取了ApplicationClassLoader，因此自然找不到class文件。
//    因此我们可以使用Thread.currentThread().getContextClassLoader()来获取WebAppClassLoader来加载，就不会报错了。


    public static Properties readPropertiesFile(String fileName) {
//      报错
//      URL url = ClassLoader.getSystemClassLoader().getResource("");

//      获取当前的classpath的绝对路径的URI表示法。
        URL url = Thread.currentThread().getContextClassLoader().getResource("");

        String rpcConfigPath = "";
        if (url != null) {
            rpcConfigPath = url.getPath() + fileName;
        }
        Properties properties = null;
        try (InputStreamReader inputStreamReader = new InputStreamReader(
                new FileInputStream(rpcConfigPath), StandardCharsets.UTF_8)) {
            properties = new Properties();
            properties.load(inputStreamReader);
        } catch (IOException e) {
            log.error("occur exception when read properties file [{}]", fileName);
        }
        return properties;
    }
}
