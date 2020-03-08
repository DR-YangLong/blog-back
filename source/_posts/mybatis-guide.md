title: MyBatis快速入门
date: 2015-09-17 17:09:56
categories: [MyBatis]
tags: [java,MyBatis]
---

> 本文只对MyBatis快速使用简介，想要了解更多信息，请前往[MyBatis]("http://mybatis.github.io/mybatis-3/zh/index.html" "userGuide" )查看官方帮助文档，部分示例也来源于此。       

因为新入同事对MyBatis不熟悉，所以写下这个指导，只含有基础常用的说明。基于Spring framework整合，MyBatis采用xml配置文件的方式。
<!-- more -->
## MyBtis映射文件
``` xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.example.dao.UserStarMapper" >
  <resultMap id="BaseResultMap" type="com.example.model.UserStar" >
    <id column="starId" property="starId" jdbcType="BIGINT" />
    <result column="userId" property="userId" jdbcType="BIGINT" />
    <result column="objectId" property="objectId" jdbcType="BIGINT" />
    <result column="objectType" property="objectType" jdbcType="CHAR" />
    <result column="starDate" property="starDate" jdbcType="TIMESTAMP" />
  </resultMap>
  <sql id="Base_Column_List" >
    starId, userId, objectId, objectType, starDate
  </sql>
  <select id="selectByPrimaryKey" resultMap="BaseResultMap" parameterType="java.lang.Long" >
    select 
    <include refid="Base_Column_List" />
    from user_star
    where starId = #{starId,jdbcType=BIGINT}
  </select>
  <delete id="deleteByPrimaryKey" parameterType="java.lang.Long" >
    delete from user_star
    where starId = #{starId,jdbcType=BIGINT}
  </delete>
  <insert id="insert" parameterType="com.example.model.UserStar" >
    insert into user_star (starId, userId, objectId, 
      objectType, starDate)
    values (#{starId,jdbcType=BIGINT}, #{userId,jdbcType=BIGINT}, #{objectId,jdbcType=BIGINT}, 
      #{objectType,jdbcType=CHAR}, #{starDate,jdbcType=TIMESTAMP})
  </insert>
  <insert id="insertSelective" parameterType="com.example.model.UserStar" >
    insert into user_star
    <trim prefix="(" suffix=")" suffixOverrides="," >
      <if test="starId != null" >
        starId,
      </if>
      <if test="userId != null" >
        userId,
      </if>
      <if test="objectId != null" >
        objectId,
      </if>
      <if test="objectType != null" >
        objectType,
      </if>
      <if test="starDate != null" >
        starDate,
      </if>
    </trim>
    <trim prefix="values (" suffix=")" suffixOverrides="," >
      <if test="starId != null" >
        #{starId,jdbcType=BIGINT},
      </if>
      <if test="userId != null" >
        #{userId,jdbcType=BIGINT},
      </if>
      <if test="objectId != null" >
        #{objectId,jdbcType=BIGINT},
      </if>
      <if test="objectType != null" >
        #{objectType,jdbcType=CHAR},
      </if>
      <if test="starDate != null" >
        #{starDate,jdbcType=TIMESTAMP},
      </if>
    </trim>
  </insert>
  <update id="updateByPrimaryKeySelective" parameterType="com.example.model.UserStar" >
    update user_star
    <set >
      <if test="userId != null" >
        userId = #{userId,jdbcType=BIGINT},
      </if>
      <if test="objectId != null" >
        objectId = #{objectId,jdbcType=BIGINT},
      </if>
      <if test="objectType != null" >
        objectType = #{objectType,jdbcType=CHAR},
      </if>
      <if test="starDate != null" >
        starDate = #{starDate,jdbcType=TIMESTAMP},
      </if>
    </set>
    where starId = #{starId,jdbcType=BIGINT}
  </update>
  <update id="updateByPrimaryKey" parameterType="com.example.model.UserStar" >
    update user_star
    set userId = #{userId,jdbcType=BIGINT},
      objectId = #{objectId,jdbcType=BIGINT},
      objectType = #{objectType,jdbcType=CHAR},
      starDate = #{starDate,jdbcType=TIMESTAMP}
    where starId = #{starId,jdbcType=BIGINT}
  </update>
</mapper>
```

## MyBatis XML文件节点介绍
### resultMap
resultMap用于将查询出来的数据库列数据封装到pojo，配合select节点的resultMap属性使用。一个xml文件可以有多个不同id的resultMap      
属性：
> id：一个namespace下只能有一个，标示这个resultMap      

> type:与此resultMap绑定的pojo      

子节点：
> 1. id:主键      
> 2. result:其他列       

column：数据库列名，如果查询时取了别名，这地方就是别名；property：数据库列名在pojo中对应的属性名，jdbcType列数据库类型名。

### insert
对应insert SQL，属性：
> 1. id: 对应DAO接口中的方法名
> 2. parameterType：参数类型，指定java类名，参数类型默认为map，下同
> 3. useGeneratedKeys：主键是否自增，true/false
> 4. keyProperty：主键对应的pojo中属性名        

insert使用主键自增时返回值为影响行数，并不是主键，要获取主键需要指定useGeneratedKeys=true且指定keyProperty，执行完后在pojo中获取keyProperty指定的属性值即为id值。
### delete
对应delete SQL，属性：
> 1. id: 对应DAO接口中的方法名
> 2. parameterType：参数类型，指定java类名      

返回影响行数。
### select
对应查询SQL，属性：
> 1. id: 对应DAO接口中的方法名
> 2. parameterType：参数类型，指定java类名
> 3. resultMap：指定返回的resultMap，值为resultMap的id，与resultType 2选一
> 4. resultType：指定返回的java pojo，此时pojo的属性名必须和查询出的列名一致，与resultMap 2选一

### update
对应更新SQL，属性:
> 1. id: 对应DAO接口中的方法名
> 2. parameterType：参数类型，指定java类名      

返回影响行数。
## xml文件与DAO绑定
> 从MyBatis以后，使用 namespace 与DAO接口进行绑定       
各节点通过id属性与DAO接口的各个方法绑定：
``` java
public interface UserStarMapper {
    int deleteByPrimaryKey(Long starId);
    int insert(UserStar record);
    int insertSelective(UserStar record);
    UserStar selectByPrimaryKey(Long starId);
    int updateByPrimaryKeySelective(UserStar record);
    int updateByPrimaryKey(UserStar record);
}
```

参数的传递：当只有一个参数时，可使用parameterType指定类型，传递多个参数时，需要封装为map类型，此时parameterType可以省略或指定为"map"，与spring整合后，可以使用注解绑定多个参数到map
> int selectNum(@Param("userId") Long userId, @Param("featureId") Long feature, @Param("draft") String draftFlag,@Param("status") String status, @Param("pushFlag") String push);      

@Param的value属性将封装为map的key，参数的值将封装为map的value。     

获取参数：在SQL中使用**\#\{\}**表达式取值或**$\{\}**表达式取值，
> \#\{pojo.name\}，$\{pojo.num\}       

\#表示取值，$表示值替换，2者的区别，如传入String类型的"b"，前者在SQL中为
> select * from table where a='b'       

后者为
> select * from table where a=b

类似于使用PreparedStatement和Statement。尽量使用\#避免SQL注入，$可以使用在动态表等地方。

## 简单查询返回数据与pojo绑定
> 1. select节点通过指定 resultMap 属性，然后resultMap通过type属性与pojo绑定     
> 2. select节点通过 resultType 与pojo绑定        

``` java
public class UserStar {
    private Long starId;
    private Long userId;
    private Long objectId;
    private String objectType;
    private Date starDate;
    //getter and setter...
    }
```

查询返回数据与pojo绑定时，在select节点使用resultMap或resultType，2者选一，resultType的值为java类全限定名（com.example.model.UserStar），或者SimpleClassName(UserStar,Spring环境下或配置了typeAliases)。
## 进阶,动态SQL
当需要使用一个sql完成不同条件组合查询时，如果分开写，就会冗余很多的代码，此时可以使用动态SQL来完成一个SQL不同组合条件的查询。同理，在java中使用for循环批量新增修改数据时，在性能上有所欠缺，此时也可以使用动态SQL来进行批量处理。
### 执行批量操作-foreach
``` xml
//省略节点属性
<select id....>
select u.id 
from user u
where u.parent in
<foreach collection="ids" open="(" close=")" separator="," item="id" index="index">
            #{id,jdbcType=BIGINT}
</foreach>
</select>
结果：
select u.id 
from user u
where u.parent in(1,2,3,4)

//省略节点属性
<insert id...>
insert into user(name,pwd) values 
<foreach collection="users" open="" close="" separator="," item="user" index="index">
            (#{user.name,jdbcType=VARCHAR},#{user.pwd,jdbcType=VARCHAR})
</foreach>
</insert>
结果：
insert into user(name,pwd) values('a','a'),('b','b'),('c','c')
```

> 1. collection:集合变量在参数map中的key，没有使用@Param指定时List实例将会以“list”作为key，而数组实例的key将是“array”
> 2. open:每次循环开始前输出
> 3. close:每次循环结束输出
> 4. separator:2次循环间的间隔输出
> 5. item:每次循环取出来的对象
> 6. index:循环下标变量

### 条件判断-if
``` xml
and:
<if test="id!=null and name!=null">
 id=#{id}
</if>
or:
<if test="id!=null or name!=null">
 id=#{id}
</if>
```

> 与java一致，test为true时输出节点的值，false时跳过，test的多条件写法，且使用AND,或使用OR

### 条件筛选trim,choose,where,set
#### trim动态拼装SQL
``` xml
<insert id="insertSelective" parameterType="com.example.model.UserStar" >
    insert into user_star
    <trim prefix="(" suffix=")" suffixOverrides="," >
      <if test="starId != null" >
        starId,
      </if>
      <if test="userId != null" >
        userId,
      </if>
      <if test="objectId != null" >
        objectId,
      </if>
      <if test="objectType != null" >
        objectType,
      </if>
      <if test="starDate != null" >
        starDate,
      </if>
    </trim>
    <trim prefix="values (" suffix=")" suffixOverrides="," >
      <if test="starId != null" >
        #{starId,jdbcType=BIGINT},
      </if>
      <if test="userId != null" >
        #{userId,jdbcType=BIGINT},
      </if>
      <if test="objectId != null" >
        #{objectId,jdbcType=BIGINT},
      </if>
      <if test="objectType != null" >
        #{objectType,jdbcType=CHAR},
      </if>
      <if test="starDate != null" >
        #{starDate,jdbcType=TIMESTAMP},
      </if>
    </trim>
</insert>
<select id="dynamicTrimTest" parameterType="Blog" resultType="Blog">  
    select * from t_blog   
    <trim prefix="where" prefixOverrides="and |or">  
        <if test="title != null">  
            title = #{title}  
        </if>  
        <if test="content != null">  
            and content = #{content}  
        </if>  
        <if test="owner != null">
            or owner = #{owner}  
        </if>  
    </trim>  
</select>  
```

> 1. prefix:开头输出
> 2. suffix:结束输出
> 3. suffixOverrides:自动判断开头或者结尾，如果有符合suffixOverrides表达式的值则去掉        

#### choose动态取一条件查询
``` xml
<select id="findActiveBlogLike"
     resultType="Blog">
  SELECT * FROM BLOG WHERE state = ‘ACTIVE’
  <choose>
    <when test="title != null">
      AND title like #{title}
    </when>
    <when test="author != null and author.name != null">
      AND author_name like #{author.name}
    </when>
    <otherwise>
      AND featured = 1
    </otherwise>
  </choose>
</select>
```

和java的switch一样，顺序匹配，匹配则中断，生成sql然后执行。
#### where动态条件查询
``` xml
使用if的动态，会造成and跟在where后的错误：
<select id="findActiveBlogLike"
     resultType="Blog">
  SELECT * FROM BLOG 
  WHERE 
  <if test="state != null">
    state = #{state}
  </if> 
  <if test="title != null">
    AND title like #{title}
  </if>
  <if test="author != null and author.name != null">
    AND author_name like #{author.name}
  </if>
</select>
使用where动态改造：
<select id="findActiveBlogLike"
     resultType="Blog">
  SELECT * FROM BLOG 
  <where> 
    <if test="state != null">
         state = #{state}
    </if> 
    <if test="title != null">
        AND title like #{title}
    </if>
    <if test="author != null and author.name != null">
        AND author_name like #{author.name}
    </if>
  </where>
</select>
```

where会自动去掉多余的and和or，等价于
```xml
<trim prefix="WHERE" prefixOverrides="AND |OR ">
  ... 
</trim>
```

#### set动态更新
``` xml
<update id="updateAuthorIfNecessary">
  update Author
    <set>
      <if test="username != null">username=#{username},</if>
      <if test="password != null">password=#{password},</if>
      <if test="email != null">email=#{email},</if>
      <if test="bio != null">bio=#{bio}</if>
    </set>
  where id=#{id}
</update>
```

只更新符合条件的字段，等价于
``` xml
<trim prefix="SET" suffixOverrides=",">
  ...
</trim>
```

### 复杂关联查询时的返回数据绑定association,collection等参见[MyBatis相关主题]("http://mybatis.github.io/mybatis-3/zh/sqlmap-xml.html#Result_Maps" "goto")