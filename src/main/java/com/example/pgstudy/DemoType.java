package com.example.pgstudy;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.UUID;

@Data
public class DemoType {
    private Integer id;
    private Integer intCol;
    private Long bigintCol;
    private Short smallintCol;
    private Integer serialCol;
    private BigDecimal decimalCol;
    private BigDecimal numericCol;
    private Float realCol;
    private Double doubleCol;
    private String charCol;
    private String varcharCol;
    private String textCol;
    private Boolean boolCol;
    private Date dateCol;
    private Time timeCol;
    private Timestamp timestampCol;
    private String intervalCol; // interval建议用String或PGInterval
    private byte[] byteaCol;
    private String jsonCol;
    private String jsonbCol;
    private UUID uuidCol;
    private Integer[] intArrayCol;
    private String[] textArrayCol;
    private String inetCol;
    private String cidrCol;
    private String macaddrCol;
    private String pointCol;
    private String lineCol;
    private String lsegCol;
    private String boxCol;
    private String pathCol;
    private String polygonCol;
    private String circleCol;
    private String xmlCol;
    private BigDecimal moneyCol;
} 