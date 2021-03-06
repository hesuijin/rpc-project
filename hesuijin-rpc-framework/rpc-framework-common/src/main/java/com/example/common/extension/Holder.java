package com.example.common.extension;

/**
 * @Description:
 *  使用volatile 锁本类
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
public class Holder<T> {

    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
