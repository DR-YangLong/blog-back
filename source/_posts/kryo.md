title: Kryo序列化
date: 2018-02-06 19:40:56
categories: [JAVA]
tags: [java]
---

# Kryo序列化
新版kryo提供了工厂类和序列化池，不用自己实现序列化池了。

## 工场类：
```java
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.pool.KryoFactory;

import java.util.HashMap;

/**
 * functional describe:Kryo工厂类，<code>registerMap</code>为需要自定义注册的类及其序列化器
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2018/2/6
 */
public class CustomKryoFactory implements KryoFactory {
    //有并发需求的话改为线程安全的map
    private HashMap<Class, Serializer> registerMap;

    @Override
    public Kryo create() {
        Kryo kryo = new Kryo();
        if (null != registerMap && registerMap.size() > 0) {
            registerMap.forEach((k, v) ->
            {
                if (v != null) {
                    kryo.register(k, v);
                } else {
                    kryo.register(k);
                }
            });
        }
        return kryo;
    }

    public HashMap<Class, Serializer> getRegisterMap() {
        return registerMap;
    }

    public void setRegisterMap(HashMap<Class, Serializer> registerMap) {
        this.registerMap = registerMap;
    }
}
```

<!-- more -->
### 序列化器
```java
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * functional describe: 使用kryo自身池实现序列化
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2018/2/6
 */
public class KryoSerializer {
    private CustomKryoFactory factory;
    private KryoPool pool;

    public KryoSerializer(CustomKryoFactory factory) {
        this.factory = factory;
        this.pool = new KryoPool.Builder(factory).build();
    }

    /**
     * 序列化
     *
     * @param obj 对象
     * @return 2进制数组
     * @throws IOException IO异常
     */
    public byte[] serialize(Object obj) throws IOException {
        byte[] bytes;
        try {
            Kryo kryo = pool.borrow();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Output output = new Output(outputStream);
            kryo.writeClassAndObject(output, obj);
            pool.release(kryo);
            output.close();
            bytes = outputStream.toByteArray();
            //help gc
            output = null;
            outputStream = null;
        } finally {
            obj = null;
        }
        return bytes;
    }

    /**
     * 反序列化
     *
     * @param bytes 输入的2进制字节数组
     * @return object
     * @throws IOException IO异常
     */
    public Object deserialize(byte[] bytes) throws IOException {
        if (bytes != null && bytes.length != 0) {
            Object obj;
            try {
                Kryo kryo = pool.borrow();
                Input input = new Input(bytes, 0, bytes.length);
                obj = kryo.readClassAndObject(input);
                input.close();
                pool.release(kryo);
            } finally {
                bytes = null;
            }
            return obj;
        } else {
            return null;
        }
    }
}
```

## 测试
```java
import cn.lvkebang.xmall.common.lang.StringSeriesTools;
import org.apache.shiro.util.Assert;

/**
 * functional describe:
 *
 * @author DR.YangLong [410357434@163.com]
 * @version 1.0    2018/2/6
 */
public class KryoTest {

    public static void main(String[] args) throws Exception{
        //随机生成字符串
        String str= StringSeriesTools.getRanDomStr(8,1);
        CustomKryoFactory factory=new CustomKryoFactory();
        KryoSerializer serializer=new KryoSerializer(factory);
        byte[] bytes=serializer.serialize(str);
        Object obj=serializer.deserialize(bytes);
        Assert.isTrue(str.equals(obj.toString()));
    }
}
```