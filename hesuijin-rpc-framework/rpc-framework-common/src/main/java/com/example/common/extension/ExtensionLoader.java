package com.example.common.extension;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.spi.ServiceRegistry;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @Description: 该类的作用是模拟 读取  yml 或者 properties 的配置文章  作用是获取相关的类
 * 了解即可
 * <p>
 * getExtensionLoader 为获取进行本类 （扩展实例类）  先进行  入参（接口）接口 的校验
 * getExtension 为入口方法   从配置文件里面获取额外信息   用于扩展类
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@Slf4j
public class ExtensionLoader<T> {

    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    private final Class<?> type;
    //对象缓存
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    //类缓存
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();


    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        //入参类型 不能为空
        if (type == null) {
            throw new IllegalArgumentException("Extension type should not be null.");
        }
        //入参类型 必须为接口
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type must be an interface.");
        }
        //该接口都需要加 @SPI注解
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }
        //先从ConcurrentHashMap获取   如果不存在
        // 则在ConcurrentHashMap  新增  接口类名称为key   ExtensionLoader<S>(接口类) 为
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        if (extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;
    }

    /**
     *  给cachedInstances  对象实例缓存
     *  新增   key为 zk  value 为 ServiceRegistryImpl 对象  的 hash
     *
     *  返回 ServiceRegistry接口类   的  实现类ServiceRegistryImpl 对象
     * @param name
     * @return
     */
    public T getExtension(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Extension name should not be null or empty.");
        }

        //在cachedInstances  获取  key为name 的value
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            //如果这个value不存在 则创建
            //同时存放到cachedInstances中   key为name  value为new出来的Holder对象
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        // 使用 holder的get方法 获取对象  注意该方法里面有 volatile关键字 可以防止重排
        Object instance = holder.get();
        // 双重判空 校验锁
        // 第一次判空 避免每次都进入 synchronized 重量级锁
        if (instance == null) {
            //使用Holder对象 进行加锁
            synchronized (holder) {
                instance = holder.get();
                //第二次判空 获取 使用入参 name  通过createExtension
                //ServiceRegistry接口类   的  实现类ServiceRegistryImpl 对象
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    /**
     * zk=com.example.demo.registryCenter.zookeeper.ServiceRegistry.ServiceRegistryImpl
     * 把资源的     等号前面的zk作为key  等号后面的接口实现类作为value  放到    名称为extensionClasses的Map集合中里面
     *
     * 返回ServiceRegistry接口类的对象
     *
     * @param name
     * @return
     */
    private T createExtension(String name) {
        //getExtensionClasses() 读取name (zk)的所有 实现类
        Map<String, Class<?>> extensionClasses = getExtensionClasses();

        //get(name) 根据名称获取 指定那个
        Class<?> clazz = extensionClasses.get(name);

        if (clazz == null) {
            throw new RuntimeException("No such extension of name " + name);
        }

        //获取该接口类的实现类
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        if (instance == null) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return instance;
    }

    /**
     * //TODO 如果配置文件被修改 如何处理
     *
     * @return
     */
    private Map<String, Class<?>> getExtensionClasses() {
        // get the loaded extension class from the cache
        Map<String, Class<?>> classes = cachedClasses.get();
        // 双重判空 校验锁
        // 第一次判空 避免每次都进入 synchronized 重量级锁
        if (classes == null) {
//            使用 synchronized 添加类锁
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                //第二次判空 获取 cachedClasses 配置信息
                if (classes == null) {
                    classes = new HashMap<>();
                    // 读取文件
                    loadDirectory(classes);
                    //cachedClasses 存放最近更新的   Map<String, Class<?>> extensionClasses
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    /**
     * 匹配到静态资源目录下的文件路径
     *入参extensionClasses 只是为了直接在该方法里面修改
     * @param extensionClasses
     */
    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        //加载 该接口类 所在包名 + 接口类名称
        //com.example.demo.registryCenter.zookeeper.ServiceRegistry.ServiceRegistry
        String fileName = ExtensionLoader.SERVICE_DIRECTORY + type.getName();
        //类加载器
        ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
        //注意  由于该路径是唯一的 所以之前获取resourceUrl就行了
        URL resourceUrl = classLoader.getResource(fileName);
        if (resourceUrl != null) {
            //读取配置文件里面资源
            loadResource(extensionClasses, classLoader, resourceUrl);
        }
    }



    /**
     * 读取配置文件的资源
     * 资源为该接口类  的实现类的路径
     *
     * 把资源的等号前面的zk作为key  等号  后面的接口实现类作为value 放到 extensionClasses map里面
     *最终目的 extensionClasses.put(name, clazz);
     *
     * @param extensionClasses
     * @param classLoader
     * @param resourceUrl
     */
    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), UTF_8))) {
            String line;
            // 读取每一行
            //
            while ((line = reader.readLine()) != null) {
                // 获取是否存在 #
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    // 如果配置文件里面带 # 号则只获取本行 # 后的资源
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0) {
                    try {
                        final int ei = line.indexOf('=');
                        String name = line.substring(0, ei).trim();
                        String clazzName = line.substring(ei + 1).trim();
                        // our SPI use key-value pair so both of them must not be empty
                        if (name.length() > 0 && clazzName.length() > 0) {
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            //把资源的等号  前面的zk作为key  等号  后面的接口实现类作为value 放到 extensionClasses map里面
                            extensionClasses.put(name, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error(e.getMessage());
                    }
                }

            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
