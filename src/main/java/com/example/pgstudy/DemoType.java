package com.example.pgstudy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.UUID;

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

    // getter/setter 省略，请根据需要自动生成
    // ...

    // 下面只写部分，实际请用IDE自动生成所有字段的getter/setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getIntCol() { return intCol; }
    public void setIntCol(Integer intCol) { this.intCol = intCol; }
    public Long getBigintCol() { return bigintCol; }
    public void setBigintCol(Long bigintCol) { this.bigintCol = bigintCol; }
    public Short getSmallintCol() { return smallintCol; }
    public void setSmallintCol(Short smallintCol) { this.smallintCol = smallintCol; }
    public Integer getSerialCol() { return serialCol; }
    public void setSerialCol(Integer serialCol) { this.serialCol = serialCol; }
    public BigDecimal getDecimalCol() { return decimalCol; }
    public void setDecimalCol(BigDecimal decimalCol) { this.decimalCol = decimalCol; }
    public BigDecimal getNumericCol() { return numericCol; }
    public void setNumericCol(BigDecimal numericCol) { this.numericCol = numericCol; }
    public Float getRealCol() { return realCol; }
    public void setRealCol(Float realCol) { this.realCol = realCol; }
    public Double getDoubleCol() { return doubleCol; }
    public void setDoubleCol(Double doubleCol) { this.doubleCol = doubleCol; }
    public String getCharCol() { return charCol; }
    public void setCharCol(String charCol) { this.charCol = charCol; }
    public String getVarcharCol() { return varcharCol; }
    public void setVarcharCol(String varcharCol) { this.varcharCol = varcharCol; }
    public String getTextCol() { return textCol; }
    public void setTextCol(String textCol) { this.textCol = textCol; }
    public Boolean getBoolCol() { return boolCol; }
    public void setBoolCol(Boolean boolCol) { this.boolCol = boolCol; }
    public Date getDateCol() { return dateCol; }
    public void setDateCol(Date dateCol) { this.dateCol = dateCol; }
    public Time getTimeCol() { return timeCol; }
    public void setTimeCol(Time timeCol) { this.timeCol = timeCol; }
    public Timestamp getTimestampCol() { return timestampCol; }
    public void setTimestampCol(Timestamp timestampCol) { this.timestampCol = timestampCol; }
    public String getIntervalCol() { return intervalCol; }
    public void setIntervalCol(String intervalCol) { this.intervalCol = intervalCol; }
    public byte[] getByteaCol() { return byteaCol; }
    public void setByteaCol(byte[] byteaCol) { this.byteaCol = byteaCol; }
    public String getJsonCol() { return jsonCol; }
    public void setJsonCol(String jsonCol) { this.jsonCol = jsonCol; }
    public String getJsonbCol() { return jsonbCol; }
    public void setJsonbCol(String jsonbCol) { this.jsonbCol = jsonbCol; }
    public UUID getUuidCol() { return uuidCol; }
    public void setUuidCol(UUID uuidCol) { this.uuidCol = uuidCol; }
    public Integer[] getIntArrayCol() { return intArrayCol; }
    public void setIntArrayCol(Integer[] intArrayCol) { this.intArrayCol = intArrayCol; }
    public String[] getTextArrayCol() { return textArrayCol; }
    public void setTextArrayCol(String[] textArrayCol) { this.textArrayCol = textArrayCol; }
    public String getInetCol() { return inetCol; }
    public void setInetCol(String inetCol) { this.inetCol = inetCol; }
    public String getCidrCol() { return cidrCol; }
    public void setCidrCol(String cidrCol) { this.cidrCol = cidrCol; }
    public String getMacaddrCol() { return macaddrCol; }
    public void setMacaddrCol(String macaddrCol) { this.macaddrCol = macaddrCol; }
    public String getPointCol() { return pointCol; }
    public void setPointCol(String pointCol) { this.pointCol = pointCol; }
    public String getLineCol() { return lineCol; }
    public void setLineCol(String lineCol) { this.lineCol = lineCol; }
    public String getLsegCol() { return lsegCol; }
    public void setLsegCol(String lsegCol) { this.lsegCol = lsegCol; }
    public String getBoxCol() { return boxCol; }
    public void setBoxCol(String boxCol) { this.boxCol = boxCol; }
    public String getPathCol() { return pathCol; }
    public void setPathCol(String pathCol) { this.pathCol = pathCol; }
    public String getPolygonCol() { return polygonCol; }
    public void setPolygonCol(String polygonCol) { this.polygonCol = polygonCol; }
    public String getCircleCol() { return circleCol; }
    public void setCircleCol(String circleCol) { this.circleCol = circleCol; }
    public String getXmlCol() { return xmlCol; }
    public void setXmlCol(String xmlCol) { this.xmlCol = xmlCol; }
    public BigDecimal getMoneyCol() { return moneyCol; }
    public void setMoneyCol(BigDecimal moneyCol) { this.moneyCol = moneyCol; }
} 