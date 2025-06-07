package com.example.pgstudy;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        initTable();

        // MyBatis配置
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        try (SqlSession session = sqlSessionFactory.openSession(true)) { // 自动提交
            UserMapper mapper = session.getMapper(UserMapper.class);

            // 插入数据
            User user1 = new User();
            user1.setName("张三");
            user1.setAge(20);
            mapper.insertUser(user1);
            System.out.println("插入成功");

            // 查询数据
            List<User> users = mapper.selectAllUsers();
            System.out.println("查询结果：");
            for (User u : users) {
                System.out.println("ID: " + u.getId() + ", Name: " + u.getName() + ", Age: " + u.getAge());
            }

            // 更新数据
            user1.setAge(25);
            mapper.updateUser(user1);
            System.out.println("更新成功");

            // 删除数据
            mapper.deleteUserByName("张三");
            System.out.println("删除成功");

            // 测试所有类型的插入、查询、更新、删除
            DemoTypeMapper demoTypeMapper = session.getMapper(DemoTypeMapper.class);
            DemoType demo = new DemoType();
            demo.setIntCol(1);
            demo.setBigintCol(1234567890123L);
            demo.setSmallintCol((short)2);
            demo.setSerialCol(10);
            demo.setDecimalCol(new java.math.BigDecimal("123.45"));
            demo.setNumericCol(new java.math.BigDecimal("678.90"));
            demo.setRealCol(3.14f);
            demo.setDoubleCol(2.718);
            demo.setCharCol("abc");
            demo.setVarcharCol("hello world");
            demo.setTextCol("这是一段文本");
            demo.setBoolCol(true);
            demo.setDateCol(java.sql.Date.valueOf("2024-06-01"));
            demo.setTimeCol(java.sql.Time.valueOf("12:34:56"));
            demo.setTimestampCol(java.sql.Timestamp.valueOf("2024-06-01 12:34:56"));
            demo.setIntervalCol("1 day 2 hours");
            demo.setByteaCol("hello bytea".getBytes());
            demo.setJsonCol("{\"a\":1,\"b\":2}");
            demo.setJsonbCol("{\"x\":true}");
            demo.setUuidCol(java.util.UUID.randomUUID());
            demo.setIntArrayCol(new Integer[]{1,2,3});
            demo.setTextArrayCol(new String[]{"a","b","c"});
            demo.setInetCol("192.168.1.1");
            demo.setCidrCol("192.168.100.128/25");
            demo.setMacaddrCol("08:00:2b:01:02:03");
            demo.setPointCol("(1,2)");
            demo.setLineCol("{1,2,3}");
            demo.setLsegCol("[(0,0),(1,1)]");
            demo.setBoxCol("((0,0),(1,1))");
            demo.setPathCol("((1,1),(2,2),(3,3))");
            demo.setPolygonCol("((0,0),(1,1),(1,0))");
            demo.setCircleCol("<(0,0),2>");
            demo.setXmlCol("<root><a>1</a></root>");
            demo.setMoneyCol(new java.math.BigDecimal("100.50"));
            demoTypeMapper.insertDemoType(demo);
            System.out.println("插入demo_types成功");

            // 查询
            var demoList = demoTypeMapper.selectAllDemoTypes();
            System.out.println("查询demo_types结果：");
            for (DemoType d : demoList) {
                System.out.println("ID: " + d.getId() + ", intCol: " + d.getIntCol() + ", jsonCol: " + d.getJsonCol());
                System.out.println("box_col: '" + d.getBoxCol() + "', path_col: '" + d.getPathCol() + "'");
                System.out.println("xml_col: '" + d.getXmlCol() + "'");
            }

            // 更新
            if (!demoList.isEmpty()) {
                DemoType first = demoList.get(0);
                first.setTextCol("更新后的文本");
                System.out.println("准备更新，box_col值为: '" + first.getBoxCol() + "'");
                demoTypeMapper.updateDemoType(first);
                System.out.println("更新demo_types成功");
            }

            // 删除
            if (!demoList.isEmpty()) {
                demoTypeMapper.deleteDemoTypeById(demoList.get(0).getId());
                System.out.println("删除demo_types成功");
            }
        }
    }

    public static void initTable() throws IOException, SQLException {

        // 1. 先确保表存在
        String url = "jdbc:postgresql://localhost:5432/root";
        String user = "root";
        String password = "123456";
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String createTable = "CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, name VARCHAR(100), age INT)";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTable);
            }
        }

        // 创建demo_types表（如已存在可忽略）
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String createDemoTypesTable = """
            CREATE TABLE IF NOT EXISTS demo_types (
                id SERIAL PRIMARY KEY,
                int_col INTEGER,
                bigint_col BIGINT,
                smallint_col SMALLINT,
                serial_col SERIAL,
                decimal_col DECIMAL(10,2),
                numeric_col NUMERIC(10,2),
                real_col REAL,
                double_col DOUBLE PRECISION,
                char_col CHAR(10),
                varchar_col VARCHAR(50),
                text_col TEXT,
                bool_col BOOLEAN,
                date_col DATE,
                time_col TIME,
                timestamp_col TIMESTAMP,
                interval_col INTERVAL,
                bytea_col BYTEA,
                json_col JSON,
                jsonb_col JSONB,
                uuid_col UUID,
                int_array_col INTEGER[],
                text_array_col TEXT[],
                inet_col INET,
                cidr_col CIDR,
                macaddr_col MACADDR,
                point_col POINT,
                line_col LINE,
                lseg_col LSEG,
                box_col BOX,
                path_col PATH,
                polygon_col POLYGON,
                circle_col CIRCLE,
                xml_col XML,
                money_col MONEY
            );
            """;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createDemoTypesTable);
            }
        }
    }
} 