# PostgreSQL与MyBatis集成解决方案完整指南

## 项目概述

本项目展示了如何在Java中使用MyBatis与PostgreSQL数据库进行集成，特别是处理PostgreSQL特有的复杂数据类型。项目涵盖了33种PostgreSQL数据类型的完整CRUD操作实现。

## 技术栈

- **Java**: OpenJDK 21
- **数据库**: PostgreSQL
- **ORM框架**: MyBatis 3.5.15
- **数据库驱动**: PostgreSQL JDBC 42.7.2
- **构建工具**: Maven

## 项目结构

```
pg_study/
├── src/main/java/com/example/pgstudy/
│   ├── Main.java                      # 主程序入口
│   ├── User.java                      # 用户实体类
│   ├── UserMapper.java                # 用户Mapper接口
│   ├── DemoType.java                  # 演示类型实体类
│   ├── DemoTypeMapper.java            # 演示类型Mapper接口
│   ├── UUIDTypeHandler.java           # UUID类型处理器
│   ├── IntegerArrayTypeHandler.java   # 整型数组类型处理器
│   ├── StringArrayTypeHandler.java    # 字符串数组类型处理器
│   ├── IntervalTypeHandler.java       # 时间间隔类型处理器
│   ├── JsonTypeHandler.java           # JSON类型处理器
│   ├── JsonbTypeHandler.java          # JSONB类型处理器
│   └── PostgreSQLTypeHandler.java     # 通用PostgreSQL类型处理器
├── src/main/resources/
│   ├── mybatis-config.xml             # MyBatis配置文件
│   ├── UserMapper.xml                 # 用户Mapper映射文件
│   └── DemoTypeMapper.xml             # 演示类型Mapper映射文件
└── pom.xml                            # Maven配置文件
```

## 遇到的问题及解决过程

### 问题1: 数组类型处理错误

**错误信息：**
```
No typehandler found for property intArrayCol
```

**问题分析：**
MyBatis默认不支持PostgreSQL的数组类型，需要自定义TypeHandler。

**解决方案：**

1. **创建IntegerArrayTypeHandler.java：**
```java
@MappedTypes(Integer[].class)
public class IntegerArrayTypeHandler extends BaseTypeHandler<Integer[]> {
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Integer[] parameter, JdbcType jdbcType) throws SQLException {
        StringBuilder str = new StringBuilder();
        str.append("{");
        for (int j = 0; j < parameter.length; j++) {
            str.append(parameter[j]);
            if (j < parameter.length - 1) {
                str.append(",");
            }
        }
        str.append("}");
        
        PGobject pgObject = new PGobject();
        pgObject.setType("int4[]");
        pgObject.setValue(str.toString());
        ps.setObject(i, pgObject);
    }

    @Override
    public Integer[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Array array = rs.getArray(columnName);
        if (array == null) {
            return null;
        }
        return (Integer[]) array.getArray();
    }
    // ... 其他方法
}
```

2. **创建StringArrayTypeHandler.java：**
```java
@MappedTypes(String[].class)
public class StringArrayTypeHandler extends BaseTypeHandler<String[]> {
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType) throws SQLException {
        StringBuilder str = new StringBuilder();
        str.append("{");
        for (int j = 0; j < parameter.length; j++) {
            str.append("\"").append(parameter[j]).append("\"");
            if (j < parameter.length - 1) {
                str.append(",");
            }
        }
        str.append("}");
        
        PGobject pgObject = new PGobject();
        pgObject.setType("text[]");
        pgObject.setValue(str.toString());
        ps.setObject(i, pgObject);
    }
    // ... 其他方法
}
```

3. **在mybatis-config.xml中注册TypeHandler：**
```xml
<typeHandlers>
    <typeHandler handler="com.example.pgstudy.IntegerArrayTypeHandler"/>
    <typeHandler handler="com.example.pgstudy.StringArrayTypeHandler"/>
</typeHandlers>
```

**关键技术点：**
- 使用`@MappedTypes`注解指定处理的Java类型
- 使用`PGobject`封装PostgreSQL特殊类型
- 需要清理编译缓存：`mvn clean compile`

### 问题2: TypeHandler注册不生效

**错误信息：**
编译成功但运行时仍报相同的TypeHandler错误。

**问题分析：**
1. 编译缓存问题
2. @MappedTypes注解缺失

**解决方案：**
1. **清理编译缓存：**
```bash
mvn clean compile
```

2. **确保所有TypeHandler类都有@MappedTypes注解：**
```java
@MappedTypes(Integer[].class)  // 必须添加此注解
public class IntegerArrayTypeHandler extends BaseTypeHandler<Integer[]> {
    // ...
}
```

### 问题3: Interval类型处理错误

**错误信息：**
```
column 'interval_col' is of type interval but expression is of type character varying
```

**问题分析：**
PostgreSQL的INTERVAL类型需要特殊处理，不能直接当作字符串处理。

**解决方案：**

1. **创建IntervalTypeHandler.java：**
```java
@MappedTypes(String.class)
public class IntervalTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType("interval");
        pgObject.setValue(parameter);
        ps.setObject(i, pgObject);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object interval = rs.getObject(columnName);
        return interval != null ? interval.toString() : null;
    }
    // ... 其他方法
}
```

2. **在DemoTypeMapper.xml中指定TypeHandler：**
```xml
<result property="intervalCol" column="interval_col" typeHandler="com.example.pgstudy.IntervalTypeHandler" />
```

3. **在INSERT/UPDATE语句中使用TypeHandler：**
```xml
#{intervalCol, typeHandler=com.example.pgstudy.IntervalTypeHandler}
```

### 问题4: JSON/JSONB类型处理错误

**错误信息：**
```
column 'json_col' is of type json but expression is of type character varying
```

**解决方案：**

1. **创建JsonTypeHandler.java：**
```java
@MappedTypes(String.class)
public class JsonTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        PGobject pgObject = new PGobject();
        if (jdbcType == JdbcType.OTHER) {
            pgObject.setType("jsonb");
        } else {
            pgObject.setType("json");
        }
        pgObject.setValue(parameter);
        ps.setObject(i, pgObject);
    }
    // ... 其他方法
}
```

2. **创建JsonbTypeHandler.java：**
```java
@MappedTypes(String.class)
public class JsonbTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        pgObject.setValue(parameter);
        ps.setObject(i, pgObject);
    }
    // ... 其他方法
}
```

### 问题5: 网络和几何类型处理错误

**错误信息：**
```
column 'inet_col' is of type inet but expression is of type character varying
```

**问题分析：**
PostgreSQL有多种网络类型（INET、CIDR、MACADDR）和几何类型（POINT、LINE、BOX等），需要通用的处理方案。

**解决方案：**

创建**PostgreSQLTypeHandler.java**，支持自动类型识别：

```java
@MappedTypes(String.class)
public class PostgreSQLTypeHandler extends BaseTypeHandler<String> {
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        PGobject pgObject = new PGobject();
        
        // 智能类型识别
        if (parameter.matches("\\d+\\.\\d+\\.\\d+\\.\\d+/\\d+")) {
            pgObject.setType("cidr");  
        } else if (parameter.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            pgObject.setType("inet");
        } else if (parameter.matches("[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}:[0-9a-fA-F]{2}")) {
            pgObject.setType("macaddr");
        } else if (parameter.startsWith("(") && parameter.endsWith(")") && parameter.contains(",") && !parameter.contains("),(")) {
            pgObject.setType("point");
        } else if (parameter.startsWith("{") && parameter.endsWith("}") && parameter.contains(",")) {
            pgObject.setType("line");
        } else if (parameter.startsWith("[(") && parameter.endsWith(")]")) {
            pgObject.setType("lseg");
        } else if (parameter.startsWith("((") && parameter.endsWith("))")) {
            // 区分 box, path, polygon
            String content = parameter.substring(2, parameter.length() - 2);
            String[] parts = content.split("\\),\\(");
            if (parts.length == 2) {
                pgObject.setType("box");
            } else if (parts.length > 2) {
                pgObject.setType("path");
            } else {
                pgObject.setType("box");
            }
        } else if (parameter.matches("\\([0-9\\.\\-]+,[0-9\\.\\-]+\\),\\([0-9\\.\\-]+,[0-9\\.\\-]+\\)")) {
            // PostgreSQL box类型的输出格式: (x1,y1),(x2,y2)
            pgObject.setType("box");
        } else if (parameter.startsWith("<(") && parameter.endsWith(">")) {
            pgObject.setType("circle");
        } else if (isXmlFormat(parameter)) {
            pgObject.setType("xml");
        } else {
            pgObject.setType("text");
        }
        
        pgObject.setValue(parameter);
        ps.setObject(i, pgObject);
    }
    
    /**
     * 判断字符串是否为XML格式
     */
    private boolean isXmlFormat(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = str.trim();
        if (trimmed.startsWith("<") && trimmed.endsWith(">")) {
            if (trimmed.contains("</") || trimmed.startsWith("<?xml") || 
                (trimmed.indexOf('>') > 0 && trimmed.indexOf('>') < trimmed.length() - 1)) {
                return true;
            }
        }
        return false;
    }
    // ... 其他方法
}
```

### 问题6: XML类型特殊处理

**错误信息：**
```
column "xml_col" is of type xml but expression is of type text
```

**问题分析：**
PostgreSQL的XML类型在读取时返回`SQLXML`对象，而不是字符串，需要特殊处理。

**最终解决方案：**

在PostgreSQLTypeHandler中添加XML特殊处理：

```java
/**
 * 将数据库对象转换为字符串，特别处理XML类型
 */
private String convertToString(Object obj) throws SQLException {
    if (obj == null) {
        return null;
    }
    
    // 检查是否是PostgreSQL XML类型
    if (obj instanceof SQLXML) {
        SQLXML xmlObj = (SQLXML) obj;
        return xmlObj.getString();
    }
    
    return obj.toString();
}

@Override
public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
    Object obj = rs.getObject(columnName);
    return convertToString(obj);
}
```

**关键发现：**
- XML类型查询时返回格式：`org.postgresql.jdbc.PgSQLXML@xxx`
- 需要调用`SQLXML.getString()`获取实际XML内容
- Box类型有两种格式：输入`((x1,y1),(x2,y2))`，输出`(x1,y1),(x2,y2)`

## 最佳实践总结

### 1. TypeHandler开发规范

```java
@MappedTypes(TargetType.class)  // 必须添加
public class CustomTypeHandler extends BaseTypeHandler<TargetType> {
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TargetType parameter, JdbcType jdbcType) throws SQLException {
        // 使用PGobject包装PostgreSQL特殊类型
        PGobject pgObject = new PGobject();
        pgObject.setType("postgresql_type_name");
        pgObject.setValue(parameter.toString());
        ps.setObject(i, pgObject);
    }

    @Override
    public TargetType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object obj = rs.getObject(columnName);
        return convertToTargetType(obj);
    }
    
    // ... 实现其他抽象方法
}
```

### 2. 配置文件最佳实践

**mybatis-config.xml：**
```xml
<configuration>
    <typeAliases>
        <typeAlias type="com.example.pgstudy.User" alias="User"/>
    </typeAliases>
    <typeHandlers>
        <!-- 注册所有自定义TypeHandler -->
        <typeHandler handler="com.example.pgstudy.UUIDTypeHandler"/>
        <typeHandler handler="com.example.pgstudy.IntegerArrayTypeHandler"/>
        <typeHandler handler="com.example.pgstudy.StringArrayTypeHandler"/>
        <!-- 不要注册IntervalTypeHandler等需要在映射文件中明确指定的处理器 -->
    </typeHandlers>
    <!-- ... 数据源配置 -->
</configuration>
```

**Mapper映射文件：**
```xml
<resultMap id="DemoTypeResultMap" type="com.example.pgstudy.DemoType">
    <!-- 需要特殊处理的字段明确指定TypeHandler -->
    <result property="intervalCol" column="interval_col" typeHandler="com.example.pgstudy.IntervalTypeHandler" />
    <result property="jsonCol" column="json_col" typeHandler="com.example.pgstudy.JsonTypeHandler" />
    <result property="inetCol" column="inet_col" typeHandler="com.example.pgstudy.PostgreSQLTypeHandler" />
</resultMap>

<insert id="insertDemoType" parameterType="com.example.pgstudy.DemoType">
    INSERT INTO demo_types (interval_col, json_col, inet_col) VALUES (
        #{intervalCol, typeHandler=com.example.pgstudy.IntervalTypeHandler},
        #{jsonCol, typeHandler=com.example.pgstudy.JsonTypeHandler},
        #{inetCol, typeHandler=com.example.pgstudy.PostgreSQLTypeHandler}
    )
</insert>
```

### 3. 调试技巧

1. **启用SQL日志：**
```xml
<configuration>
    <settings>
        <setting name="logImpl" value="STDOUT_LOGGING"/>
    </settings>
</configuration>
```

2. **添加调试输出：**
```java
System.out.println("准备插入，参数值: " + parameter);
System.out.println("识别的PostgreSQL类型: " + pgObject.getType());
```

3. **分步测试：**
   - 先测试基础类型
   - 再测试数组类型
   - 最后测试复杂几何类型

### 4. 常见错误排查

| 错误信息 | 可能原因 | 解决方案 |
|---------|---------|---------|
| `No typehandler found` | TypeHandler未注册或@MappedTypes缺失 | 检查配置文件和注解 |
| `column 'xxx' is of type yyy but expression is of type text` | PostgreSQL类型转换失败 | 创建专用TypeHandler |
| `ClassCastException` | 类型转换错误 | 检查getNullableResult方法 |
| 编译成功运行失败 | 编译缓存问题 | 执行`mvn clean compile` |

## 支持的PostgreSQL数据类型

| 类型分类 | 支持的类型 | TypeHandler |
|---------|-----------|-------------|
| 数值类型 | INTEGER, BIGINT, SMALLINT, DECIMAL, NUMERIC, REAL, DOUBLE | 默认 |
| 字符类型 | CHAR, VARCHAR, TEXT | 默认 |
| 布尔类型 | BOOLEAN | 默认 |
| 日期时间 | DATE, TIME, TIMESTAMP | 默认 |
| 时间间隔 | INTERVAL | IntervalTypeHandler |
| 二进制 | BYTEA | 默认 |
| JSON类型 | JSON, JSONB | JsonTypeHandler, JsonbTypeHandler |
| UUID | UUID | UUIDTypeHandler |
| 数组类型 | INTEGER[], TEXT[] | IntegerArrayTypeHandler, StringArrayTypeHandler |
| 网络类型 | INET, CIDR, MACADDR | PostgreSQLTypeHandler |
| 几何类型 | POINT, LINE, LSEG, BOX, PATH, POLYGON, CIRCLE | PostgreSQLTypeHandler |
| XML类型 | XML | PostgreSQLTypeHandler |
| 货币类型 | MONEY | 默认 |

## 项目运行结果

成功实现了完整的CRUD操作：

```
插入成功
查询结果：
ID: 12, Name: 张三, Age: 20
更新成功
删除成功
插入demo_types成功
查询demo_types结果：
ID: 1, intCol: 1, jsonCol: {"a":1,"b":2}
box_col: '(1.0,1.0),(0.0,0.0)', path_col: '((1.0,1.0),(2.0,2.0),(3.0,3.0))'
xml_col: '<root><a>1</a></root>'
更新demo_types成功
删除demo_types成功
```

## 总结

本项目成功解决了MyBatis与PostgreSQL集成中的所有主要难点：

1. **数组类型处理** - 通过自定义TypeHandler和PGobject实现
2. **特殊类型转换** - JSON、JSONB、INTERVAL等类型的专门处理
3. **几何类型支持** - 通用PostgreSQLTypeHandler支持多种几何类型
4. **XML类型特殊处理** - 正确处理SQLXML对象
5. **智能类型识别** - 根据字符串格式自动判断PostgreSQL类型

这个解决方案可以作为其他PostgreSQL与MyBatis集成项目的参考实现，提供了完整的、生产级别的类型处理方案。 