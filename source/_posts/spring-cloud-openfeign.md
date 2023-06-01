title: OpenFeign SpringCloud下传参简要说明
date: 2023-05-30 19:01:13
categories: [feign]
tags: [java,feign]
---

# OpenFeign

feign本质是通过解析接口及方法，参数的注解，生成HTTP客户端，进行http调通。但相对于自己手动写http进行调用，有一些限制。

## 基础配置

feign可以单独使用，但大多数情况还是在Spring Cloud应用中使用。

### pom配置

```text
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-httpclient</artifactId>
</dependency>
<!--        <dependency>-->
<!--            <groupId>io.github.openfeign</groupId>-->
<!--            <artifactId>feign-okhttp</artifactId>-->
<!--        </dependency>-->
<!-- http client end -->
```

### yml配置

```yml
feign:
  client:
    config:
      # 默认全局配置，如果要单独对某个服务进行配置，将default换为服务名，即@FeignClient的value属性值
      default:
        # 建立链接超时时间
        connect-timeout: 1500
        # 读取超时时间
        read-timeout: 5000
        # 日志级别
        loggerLevel: basic
  # 使用commons http，需要依赖在classpath
  httpclient:
    enabled: true
    max-connections: 2000
    max-connections-per-route: 100
    connection-timeout: 1500
    time-to-live: 30
    time-to-live-unit: minutes
    follow-redirects: true
  # 需要相关依赖在classpath
  circuitbreaker:
    enabled: true
  # 压缩配置
  compression:
    request:
      enabled: true
      # 大于此值进行压缩
      min-request-size: 2048
      # 什么参数类型的请求进行压缩
      mime-types: text/xml,application/xml,application/json
    response:
      enabled: true
```

## cloud环境下使用

### 启用

在配置类上进行启用
> @EnableFeignClients(basePackages = {"dr.yanglong.demo.consumer.feign"})

### 编写客户端

```java

@FeignClient(value = "demo-service", url = "${demo.service.url:}", path = "${demo.service.path:}", contextId = "demo-consumer")
public interface DemoApi {

    /**
     * GET请求示例
     * <p>
     * 1. 从environment中提取app.cdn添加到请求头中，其他请求方法方法同理
     * 2. 方法参数index作为路径替换请求路径，其他请求方法方法同理
     * 3. 方法参数tag作为请求参数添加到查询参数列表，其他请求方法方法同理
     * 4. 方法参数token添加到请求头中，@RequestHeader可以注解在Map实现类上或HttpHeaders上传递多个header，其他请求方法方法同理
     * 5. 方法参数user各个属性被提取添加到查询参数列表，其他请求方法方法同理
     *
     * @param index path参数示例
     * @param tag   单参数示例
     * @param token 请求头示例
     * @param user  对象参数示例
     * @return 值
     */
    @GetMapping(value = "search/{index}", headers = {MediaType.APPLICATION_JSON_VALUE, "CDN-NODE=${app.cdn}"})
    User search(@PathVariable(value = "index") String index,
                @RequestParam(value = "tag") String tag,
                @RequestHeader(value = "token") String token,
                @SpringQueryMap User user);

    /**
     * POST请求示例
     *
     * @param user  body参数
     * @param group query参数
     * @param headers 请求头，其他请求方法方法同理
     */
    @PostMapping("add")
    void add(@RequestBody User user, @RequestParam("group") String group, @HeaderMap Map<String, Object> headers);
}
```
<!--more -->

## 传参说明

| 传参方式   |      支持方法      | Feign方法参数处理                                                                                              | 服务提供方参数处理            |
|:-------|:--------------:|:---------------------------------------------------------------------------------------------------------|:---------------------|
| query  |      ALL       | 基本类型及其包装类型参数、Map参数使用@RequestParam注解，其他对象参数使用@SpringQueryMap注解方法参数                                        | 使用对象接收或者多参数列表接收      |
| body   | POST、PUT、PATCH | 使用@RequestBody注解方法参数                                                                                     | 参数注解@RequestBody接收   |
| header |      ALL       | @RequestHeader注解方法参数传递单个或多个请求头，@HeaderMap注解Map方法参数传递多个， @Headers注解方法上传递多个，Spring restful方法注解headers中传递多个 | 参数注解@RequestHeader接收 |
| path   |      ALL       | @PathVariable注解方法参数                                                                                      | 参数注解@PathVariable接收  |

### 日期参数处理

> @RequestParam注解的日期参数（query方式）：参数使用@DateTimeFormat一起注解，调用方和被调用方日期格式保持一致。使用的是FeignFormatterRegistrar注册的converter或formatter。

> @SpringQueryMap注解的对象中含有的日期参数: 如果使用默认编码器，使用@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)注解在属性上，被调用方必须使用相同的注解；如果使用自定义QueryMapEncoder，需要调用方与被调用方保持日期格式一致。复杂参数和对象参数建议直接使用body传递。

> @RequestBody注解的对象中含有的日期参数：默认最终会使用定义的jackson ObjectMapper进行日期转字符串，调用方和被调用方保持日期格式一致。

> query方式的日期参数还可以通过实现FeignFormatterRegistrar接口并注入自己扩展的converter或formatter进行转换处理，将实现类注入IOC容器即可，同样需要调用方与被调用方日期格式保持一致。如下：

```java
import com.alibaba.fastjson.JSON;
import dr.yanglong.demo.model.User;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * 用途描述： Converter和Formatter的区别，Formatter既用于参数处理，也用于响应处理，Converter只用于参数处理
 *
 * @author YangLong
 * @version V1
 * @since V1
 */
@Component
public class DateFormatterRegistrar implements FeignFormatterRegistrar {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH-mm-ss";
    public static final String DATE_TIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;


    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addConverter(Date.class, String.class, new DateConvert());
        registry.addConverter(LocalDate.class, String.class, new LocalDateConvert());
        registry.addConverter(LocalTime.class, String.class, new LocalTimeConvert());
        registry.addConverter(LocalDateTime.class, String.class, new LocalDateTimeConvert());
        registry.addFormatter(new UserFormatter());
    }


    public class DateConvert implements Converter<Date, String> {
        @Override
        public String convert(@NonNull Date source) {
            return DateFormatUtils.format(source, DATE_TIME_PATTERN);
        }
    }

    public class LocalDateConvert implements Converter<LocalDate, String> {
        @Override
        public String convert(@NonNull LocalDate source) {
            return DateTimeFormatter.ofPattern(DATE_PATTERN).format(source);
        }
    }

    public class LocalTimeConvert implements Converter<LocalTime, String> {
        @Override
        public String convert(@NonNull LocalTime source) {
            return DateTimeFormatter.ofPattern(TIME_PATTERN).format(source);
        }
    }

    public class LocalDateTimeConvert implements Converter<LocalDateTime, String> {
        @Override
        public String convert(@NonNull LocalDateTime source) {
            return DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).format(source);
        }
    }

    public class UserFormatter implements Formatter<User> {

        /**
         * 用于响应转换，将字符串转换到User
         *
         * @param text   the text string
         * @param locale the current user locale
         */
        @Override
        public User parse(@NonNull String text, @NonNull Locale locale) throws ParseException {
            return JSON.parseObject(text, User.class);
        }

        /**
         * 用于请求转换，将对象转换为字符串参数
         *
         * @param object the instance to print
         * @param locale the current user locale
         */
        @Override
        public String print(@NonNull User object, @NonNull Locale locale) {
            return JSON.toJSONString(object);
        }
    }

}
```

### 数组参数和LIST参数

以下情况仅适用于默认消息转换器行为。如果自定义了相关消息转换器，则不适用。

| 调用方参数                                 | 被调用方参数                                     |
|:--------------------------------------|:-------------------------------------------|
| @RequestParam("ids") List<String> ids | ArrayList<String> ids                      |
| @RequestParam("ids") List<String> ids | String[] ids                               |
| @RequestParam("ids") String[] idS     | String[] ids                               |
| @RequestParam("ids") String[] ids     | @RequestParam("ids") ArrayList<String> ids |
| @RequestBody List<User> users         | @RequestBody List<User> users              |

> List参数传递，如果传递复杂对象，@RequestParam默认情况下无法传递，使用@RequestBody可以传递，日期属性与上节说明一致。

> List简单参数传递，可以使用同名List实现类或者同类型数组进行接收

> 不扩展的情况下数组无法传递复杂参数，数组参数，可以使用同名数组或者同类型List实现类接收

### MAP参数

@RequestParam注解Map参数，不需要指定value属性，不建议使用。

## 扩展

openfeign本身提供了很多扩展接口，比如Encoder、Decoder、QueryMapEncoder、ErrorDecoder、RequestInterceptor、ResponseInterceptor等

### 扩展对象参数处理

> feign.QueryMapEncoder 处理请求参数类型为query，注解了@Param【@RequestParam】的参数，注解了@QueryMap【@SpringQueryMap】的参数对象

> feign.codec.Encoder 处理POST、PUT、PATCH请求，请求参数类型为body。一种是注解了@RequestBody，一种是传递对象参数且参数没有任何注解

QueryMapEncoder实现参考feign.querymap.BeanQueryMapEncoder，简单来说就是将带有@SpringQueryMap注解的对象，解析为Map<
String,Object>，属性名为key，属性值为value。
Encoder实现参考JacksonEncoder，此接口主要实现的功能是将@RequestBody注解的对象，转换为byte数组放入RequestTemplate的body中。

实现了QueryMapEncoder和Encoder以后，可以在配置文件中进行配置启用。

```yaml
feign:
  client:
    config:
      demo-service:
        # 配置扩展的encoder实现类
        encoder: feign.jackson.JacksonEncoder
        # 配置扩展的QueryMapEncoder实现类
        query-map-encoder: feign.querymap.BeanQueryMapEncoder
        decoder: feign.jackson.JacksonDecoder
```

### 扩展响应对象处理

> feign.codec.Decoder 用户于处理响应体，将响应内容转换为指定对象

Decoder实现参考JacksonDecoder。
配置中启用：

```yaml
feign:
  client:
    config:
      demo-service:
        # 配置扩展的Decoder实现类
        decoder: feign.jackson.JacksonDecoder

```

### 扩展拦截器

RequestInterceptor拦截器用于在请求参数解析完成后，请求发出前对请求进行修改，如添加额外的请求头，添加签名，修改参数值【不推荐】等。
ResponseInterceptor拦截器用于在请求完成后，对响应解析进行切面around处理，比如添加额外的响应头，对解码后的返回值进行验签，或者替换返回值等。此拦截器一般没啥用，官方也不提供配置文件方式配置。

RequestInterceptor和ResponseInterceptor都比较简单，只要实现规定的方法即可。
配置RequestInterceptor：

```yaml
feign:
  client:
    config:
      demo-service:
        # 配置扩展的请求拦截器实现类
        request-interceptors:
          - sameple.DemoInterceptor
```

### 扩展参数注解

自定义参数注解，需要实现AnnotatedParameterProcessor接口，并关联Feign contract（作为Bean注入IOC容器即可，但这种方式会造成默认处理器失效）。
AnnotatedParameterProcessor用于解析参数注解并将解析后的信息添加到MethodMetaData中。
此扩展在初始化时执行，用于生成Http方法模板（MethodHandler，方法参数关联Expander），不会涉及到某个具体请求参数的转换。

！！！强烈不建议进行扩展，使用Spring Cloud open feign提供的细粒度扩展即可满足需求

> 定义自定义参数注解

```java
public @interface QueryParam {
    String value() default "";
}
```

> 实现AnnotatedParameterProcessor接口

```java
import feign.MethodMetadata;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import static feign.Util.checkState;
import static feign.Util.emptyToNull;

/**
 * 用途描述：解析自定义参数注解，生成方法参数元信息
 *
 * @author YangLong
 * @version V1.0
 * @see org.springframework.cloud.openfeign.annotation.QueryMapParameterProcessor
 * @since 2023/5/26
 */
@Component
public class QueryParamParameterProcessor implements AnnotatedParameterProcessor {
    private static final Class<QueryParam> ANNOTATION = QueryParam.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    /**
     * @param context    参数上下文
     * @param annotation QueryParam注解实例
     * @param method     feign接口方法
     * @return 处理结果
     */
    @Override
    public boolean processArgument(AnnotatedParameterContext context, Annotation annotation, Method method) {
        //获取当前参数在方法参数列表中的位置
        int parameterIndex = context.getParameterIndex();
        //获取参数类型
        Class<?> parameterType = method.getParameterTypes()[parameterIndex];
        //获取方法元数据
        MethodMetadata data = context.getMethodMetadata();
        //如果是MAP，忽略其他的参数，直接设置并返回
        if (Map.class.isAssignableFrom(parameterType)) {
            checkState(data.queryMapIndex() == null, "Query map can only be present once.");
            data.queryMapIndex(parameterIndex);
            return true;
        }
        //从注解获取参数名
        QueryParam queryParam = ANNOTATION.cast(annotation);
        String name = queryParam.value();
        //请求参数名不能为空
        checkState(emptyToNull(name) != null, "QueryParam.value() was empty on parameter %s", parameterIndex);
        //设置参数名
        context.setParameterName(name);
        //设置元数据
        Collection<String> query = context.setTemplateParameter(name, data.template().queries().get(name));
        data.template().query(name, query);
        return true;
    }
}
```

> 关联contract并注册

```java

@Configuration
public class FeignGlobalConfig {
    @Autowired(required = false)
    private FeignClientProperties feignClientProperties;

    @Autowired
    private FormattingConversionService feignConversionService;

    @Bean
    public Contract feignContract() {
        boolean decodeSlash = feignClientProperties == null || feignClientProperties.isDecodeSlash();
        List<AnnotatedParameterProcessor> parameterProcessors = new ArrayList<>(8);
        parameterProcessors.add(new CookieValueParameterProcessor());
        parameterProcessors.add(new MatrixVariableParameterProcessor());
        parameterProcessors.add(new PathVariableParameterProcessor());
        parameterProcessors.add(new QueryMapParameterProcessor());
        parameterProcessors.add(new RequestHeaderParameterProcessor());
        parameterProcessors.add(new RequestParamParameterProcessor());
        parameterProcessors.add(new RequestPartParameterProcessor());
        parameterProcessors.add(new QueryParamParameterProcessor());
        return new SpringMvcContract(parameterProcessors, feignConversionService, decodeSlash);
    }
}
```

## DEBUG

Feign最终的请求都是由HttpClient发出的。

1. 如果用的是Apache HttpClient，在底层feign.httpclient.ApacheHttpClient的execute方法中debug即可。
2. 如果用的是OkHttp，在feign.okhttp.OkHttpClient的execute方法中debug即可。
3. 如果没有引入第三方http client框架，在feign.Client.Default的execute方法中debug即可。
4. 使用了服务发现，服务名到IP端口的转换可以在org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient.execute中debug即可，此方法最终会使用OkHttp或者ApacheClient进行HTTP调用。
5. 对feign.SynchronousMethodHandler的invoke进行debug，可以看到当前feign执行方法解析的Http模板，decoder，encoder，interceptor等配置。
