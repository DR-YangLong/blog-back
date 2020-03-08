title: Druid加密
date: 2017-05-28 19:40:56
categories: [java]
tags: [others]
---

# Druid数据源加密
首先说下意见，druid数据源加密其实可有可无，只不过是多了一点破解时间，因为公钥和密文都知道，破解非常简单。不过加密下还是有些许必要的，至少逼格档次和心里安慰是有的:)。

## ConfigTools.java加密
druid的加密类为ConfigTools，全路径为：
> com.alibaba.druid.filter.config.ConfigTools.java

里面有个main方法，随机生成私钥和公钥，并加密密码生成密文。所以使用时只需要传入密码即可，运行后会输出公钥，私钥和密码密文。

> java -cp druid-1.0.26.jar com.alibaba.druid.filter.config.ConfigTools root

<!-- more -->
```txt
privateKey:MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAiMmH7UHifMOk7lYYfwnH4yJ7mg7BrJ6MxUlZeisp4v2mBF70byat5Rh12K4xtwTmn53KOg5CwTCG8gDHQkwq/wIDAQABAkBsSOYDC5opXJPvXVbNEsgbZgvlHHNznJwh/fwIe60QFLtZNIImtM8ar+tFK+Vg/cwpmPyaOS57M2msBA31mSX5AiEA9L53/4hkinrQca2QIAWWpHx3QPwdQsJbRBHycnPxKfsCIQCPFAQzbbz7nLIV78flw1EhSEanJewFzSJ1xquFSbgXzQIhAIvkYwq4l19gG80efAcRqq2SR0fp03/ZP/mEmKQ7CVoJAiBiSLK2DU0DXllq/kLHz0q83SRDj6Y5OffRBb8vGTvPhQIhAMmrCgtPPsMz+R076967CuD3zwa6rhmR9yXZXf4Nc4Xd

publicKey:MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIjJh+1B4nzDpO5WGH8Jx+Mie5oOwayejMVJWXorKeL9pgRe9G8mreUYddiuMbcE5p+dyjoOQsEwhvIAx0JMKv8CAwEAAQ==

password:U8TkvAncYH6sYhuHbYsF2bvhk+O+hC9RLhKPo1+u/cuLW/WVxlxaJ+WUkSffL7sZ5EJAtvjNodNafM3kDnmc6Q==
```
网上有很多都是老版本的，使用的都是默认私钥和公钥，不过效果没差别。
main方法源码：    
```java
	public static void main(String[] args) throws Exception {
        String password = args[0];
        String[] arr = genKeyPair(512);
        System.out.println("privateKey:" + arr[0]);
        System.out.println("publicKey:" + arr[1]);
        System.out.println("password:" + encrypt(arr[0], password));
	}
```

## 配置
```yml
#data source配置下
password: "密文密码"
connectionProperties: "config.decrypt=true;config.decrypt.key=公钥"
# stat是状态监控，可参照官方文档配置，如果只用解密密码，只需配置config参数
filters: "stat,config"
```

## 结语
其实意义不是很大，因为如果别人已经拿到你的密文和公钥，那加密和解密都没有任何意义，如果是通过抓包获取，那获取的时候已经是明文了。数据库的安全还是需要依赖于操作系统的防火墙和其他设施来保障的，比如iptables这些。