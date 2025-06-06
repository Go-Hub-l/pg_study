package com.example.pgstudy;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.*;

@MappedTypes(String.class)
public class PostgreSQLTypeHandler extends BaseTypeHandler<String> {
    
    private String pgTypeName;
    
    public PostgreSQLTypeHandler() {
        // 默认构造函数
    }
    
    public PostgreSQLTypeHandler(String pgTypeName) {
        this.pgTypeName = pgTypeName;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        PGobject pgObject = new PGobject();
        
        // 根据参数自动识别类型或使用指定类型
        if (pgTypeName != null) {
            pgObject.setType(pgTypeName);
        } else {
            // 改进的自动识别逻辑
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
                    pgObject.setType("box");  // box has exactly 2 points
                } else if (parts.length > 2) {
                    // 假设超过2个点的是path，闭合的多边形用不同格式
                    pgObject.setType("path");
                } else {
                    pgObject.setType("box");  // fallback
                }
            } else if (parameter.matches("\\([0-9\\.\\-]+,[0-9\\.\\-]+\\),\\([0-9\\.\\-]+,[0-9\\.\\-]+\\)")) {
                // PostgreSQL box类型的输出格式: (x1,y1),(x2,y2)
                pgObject.setType("box");
            } else if (parameter.startsWith("<(") && parameter.endsWith(">")) {
                pgObject.setType("circle");
            } else if (isXmlFormat(parameter)) {
                pgObject.setType("xml");
            } else {
                // 为了安全起见，让PostgreSQL自己处理类型转换
                pgObject.setType("text");
            }
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
        // 基本XML格式检查：以<开始，以>结束，包含标签
        if (trimmed.startsWith("<") && trimmed.endsWith(">")) {
            // 进一步检查是否包含XML标签结构
            if (trimmed.contains("</") || trimmed.startsWith("<?xml") || 
                (trimmed.indexOf('>') > 0 && trimmed.indexOf('>') < trimmed.length() - 1)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object obj = rs.getObject(columnName);
        return convertToString(obj);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object obj = rs.getObject(columnIndex);
        return convertToString(obj);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object obj = cs.getObject(columnIndex);
        return convertToString(obj);
    }
    
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
} 