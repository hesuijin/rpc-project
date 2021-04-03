package com.example.common.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 获取单例对象的工厂类
 * @Author HeSuiJin
 * @Date 2021/4/4
 */
//TODO 待确认加锁的方式
public class SingletonFactory {

    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

//    volatile 防止创建对象时重排
//    private static volatile Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    private SingletonFactory() {
    }

   private static Object lock = new Object();

    public static <T> T getInstance(Class<T> c) {

        //使用synchronized进行加锁
        synchronized (lock) {

            String key = c.toString();
            if (c == null) {
                throw new IllegalArgumentException();
            }

            if (OBJECT_MAP.containsKey(key)) {
                return c.cast(OBJECT_MAP.get(key));
            } else {
                return c.cast(OBJECT_MAP.computeIfAbsent(key, k -> {
                    try {
                        return c.getDeclaredConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }));
            }

        }

    }
}
