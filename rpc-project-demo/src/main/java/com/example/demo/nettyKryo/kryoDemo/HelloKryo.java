package com.example.demo.nettyKryo.kryoDemo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.example.demo.nettyKryo.dto.SomeClass;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2023/3/11
 */
public class HelloKryo {

    public static void main(String[] args) throws Exception {
            Kryo kryo = new Kryo();
            kryo.register(SomeClass.class);

            //输出 因此需要写入到kryo
            SomeClass objectOutputForWrite = new SomeClass();
            objectOutputForWrite.setValue("Hello Kryo!");

            Output output = new Output(new FileOutputStream("fileforKryo.bin"));
            kryo.writeObject(output, objectOutputForWrite);
            output.close();

             //输入 因此需要从kryo读取
            Input input = new Input(new FileInputStream("fileforKryo.bin"));
            SomeClass objectInputForRead = kryo.readObject(input, SomeClass.class);
            input.close();

            //最终通过Kryo获取结果
            System.out.println(objectInputForRead.getValue());
    }
}
