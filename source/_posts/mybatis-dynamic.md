title: MyBatis动态表
date: 2015-09-17 18:40:56
categories: [MyBatis]
tags: [java,MyBatis]
---

## 场景
有很多的数据，但是数据结构完全一致，根据数据来源存放不同的表，数据库中间件完全能做到，但代价太大。由于只是简单入库，所以使用MyBatis中的\$\{\}表达式替换表名进行入库，同时返回入库记录id。
对只使用EL ($\{\})，同时使用ONGL(\#\{\})以及混用做测试。jdbc链接最好指定允许多查询
> allowMultiQueries=true		

<!-- more -->
## 示例
``` xml
	<select id="selectByPrimaryKey" resultMap="BaseResultMap"
            statementType="STATEMENT">
        select
        *
        from ${tableName}
        where id = ${id}
    </select>

    <select id="selectByDataHash" resultType="java.lang.Integer">
        select count(0) from ${tableName} where data_md5=#{dataMd5}
    </select>

    <delete id="deleteByPrimaryKey" statementType="STATEMENT">
    delete from ${tableName}
    where id = ${id}
    </delete>

    <select id="insertByNativeSQL" resultType="int">
        ${nativeSQL};
        SELECT last_insert_id() as id
    </select>

    <select id="insertByMap" parameterType="map" resultType="java.lang.Integer">
        insert into ${tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="id != null">
                id,
            </if>
            <if test="comId != null">
                com_id,
            </if>
            <if test="comName != null">
                com_name,
            </if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="id != null">
                #{id},
            </if>
            <if test="comId != null">
                #{comId},
            </if>
            <if test="comName != null">
                #{comName},
            </if>
        </trim>;
        select last_insert_id() as id
    </select>

    <select id="insertSelective" resultType="java.lang.Integer">
        insert into ${tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="comId != null">
                com_id,
            </if>
            <if test="comName != null">
                com_name,
            </if>
            <if test="appDomain != null">
                app_domain,
            </if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="comId != null">
                #{comId},
            </if>
            <if test="comName != null">
                #{comName},
            </if>
            <if test="appDomain != null">
                #{appDomain},
            </if>
        </trim>;
        select last_insert_id() as id
    </select>

    <select id="insertByDomain" resultType="java.lang.Integer">
        insert into ${tableName}
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="data.id != null">
                id,
            </if>
            <if test="data.comId != null">
                com_id,
            </if>
            <if test="data.comName != null">
                com_name,
            </if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="data.id != null">
                #{data.id},
            </if>
            <if test="data.comId != null">
                #{data.comId},
            </if>
            <if test="data.comName != null">
                #{data.comName},
            </if>
        </trim>;
        select last_insert_id() as id
    </select>
```


``` java
    int deleteByPrimaryKey(@Param("id") Integer id, @Param("tableName") String tableName);
    Integer insertSelective(@Param("comId") Integer comId, @Param("comName") String comName, @Param("appDomain") String appDomain,@Param("tableName") String tableName);
    Integer insertByMap(Map map);
    ComData selectByPrimaryKey(@Param("id") Integer id, @Param("tableName") String tableName);
    int insertByNativeSQL(@Param("nativeSQL") String SQL);
    Integer insertByDomain(@Param("tableName") String tableName, @Param("data") ComData data);
    Integer selectByDataHash(@Param("tableName")String tableName,@Param("dataMd5")String dataMd5);
```


说明，只是用EL表达式时，必须指定statementType="STATEMENT"，混用时只能使用默认的"PREPARED"。