package com.example.common.extension;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @Description: 该类的作用是通过读取自定义的配置文件,通过单例模式获取相关的接口类的实现类.
 * getExtensionLoader 获取类的加载类,进行类型入参的校验.
 * getExtensionInstance 为入口方法   从配置文件里面获取其接口实现类(类的扩展类) 的对应对象.
 * @Author HeSuiJin
 * @Date 2023/3/19
 */
@Slf4j
public class ExtensionLoader<T> {

    //配置文件路径前缀
    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";

    //缓存到内存里面的类对象的 类型
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    //缓存到内存里面的类对象的 实例
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();
    //类的类型
    private final Class<?> type;
    //对象缓存
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    //类缓存
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    //构造方法
    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    // 使用泛型方法获取加载类的类对象：后续用于获取接口类的实现类
    // 在获取加载类的类对象的过程中需判断泛型入参的类型是否正确
    // 泛型方法
    // 划分： 1：<S>   2：ExtensionLoader<S>    3：getExtensionLoader(Class<S> type)
    // 1:<S>  代表使用S 代表泛型的类型 可以认为是定义了该方法的一个全局参数
    //        把 ExtensionLoader<S>   getExtensionLoader(Class<S> type)以及方法里面的使用了<S>  才允许使用该<S>
    // 2:ExtensionLoader<S>  返回的类型
    // 3:getExtensionLoader(Class<S> type) 入参的类型  （可以认为是）
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
        //则在ConcurrentHashMap 新增  接口类名称为key   ExtensionLoader<S>(接口类) 为
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        if (extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<S>(type));
            //类对象的类型
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;
    }

    /**
     *  根据入参name获取对象实例  （这样获取的对象实例为单例 支持重复获取）
     *  从cachedInstances  对象实例缓存
     *      如果存在则获取
     *      如果不存在则使用name为key  执行创建该类对象实例的逻辑  创建完后存放到cachedInstances  后续直接使用该单例即可
     *  获取过程中需要对该类对象加锁
     * @param name （key）
     * @return 类对象
     */
    // public T getExtension(String name) 并不是泛型方法 ，这里仅是用了泛型类所设置的类型变量T,
    // 使用 <T>才是泛型方法。
    public T getExtensionInstance(String name) {
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
            //类的实例对象的维度进行加锁,防止并发创建单例. 上方已经使用 name 进行 new Holder<>()的动作
            synchronized (holder) {
                instance = holder.get();
                //第二次判空 获取 使用入参 name  通过createExtension
                //ServiceRegistry接口类   的  实现类ServiceRegistryImpl 对象
                if (instance == null) {
                    instance = getExtensionByName(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    /**
     * 获取name对应的接口类的实现类对应的类对象
     * @param name  （key）
     * @return  类对象
     */
    // public T createExtension(String name) 并不是泛型方法 ，这里仅是用了泛型类所设置的类型变量T,
    // 使用 <T>才是泛型方法。
    private T getExtensionByName (String name) {
        //getExtensionClasses() 所有实现类
        Map<String, Class<?>> extensionClasses = getExtensionClasses();

        //get(name) 根据名称获取 指定那对应的key
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
     * getExtensionClasses  获取指定配置文件的接口类的实现类
     *  1：从cachedClasses中获取
     *  2：如果cachedClasses中没有 则读取配置文件生成 生成后存放到cachedClasses
     * @return 全部的接口实现类(类的扩展类)
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
     * 创建本类的类加加载器 并  匹配到静态资源目录下的文件路径
     *入参extensionClasses 只是为了直接在该方法里面修改扩展类
     * @param extensionClasses 配置文件里面的：全部的类的扩展类（实现类）
     */
    private void loadDirectory(Map<String, Class<?>> extensionClasses) {
        //加载 该接口类 所在包名 + 接口类名称
        //"META-INF/extensions/" +  创建泛型类中的类的类型 的name
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
     * 读取配置文件的资源  使用类加载器classLoader进行创建
     * 把资源文件内容中  等号前面的作为key  等号后面的接口类的实现类作为value  最后放到extensionClasses map里面
     * 最终目的 extensionClasses.put(name, clazz)
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
