package com.example.demo.nettyKryo.kryoDemo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.example.demo.nettyKryo.dto.Account;
import com.example.demo.nettyKryo.dto.User;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2023/3/11
 */
public class ReferencesKryo {

    public static void main(String[] args) throws Exception {
        Kryo kryo = new Kryo();
        kryo.register(User.class);
        kryo.register(Account.class);

        User user = new User();
        user.setUsername("hesuijin");

        Account account = new Account();
        account.setAccountNo("10000");

        // 循环引用
        user.setAccount(account);
        account.setUser(user);

        // 这里需要设置为true，才不会报错
        kryo.setReferences(false);

        Output output = new Output(new FileOutputStream("kryoreference.bin"));
        kryo.writeObject(output, user);
        output.close();

        Input input = new Input(new FileInputStream("kryoreference.bin"));
        User objectInput = kryo.readObject(input, User.class);
        input.close();
        System.out.println(objectInput.getUsername());
        System.out.println(objectInput.getAccount().getAccountNo());

    }
}