package com.example.pgstudy;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.*;

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

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object json = rs.getObject(columnName);
        return json != null ? json.toString() : null;
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object json = rs.getObject(columnIndex);
        return json != null ? json.toString() : null;
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object json = cs.getObject(columnIndex);
        return json != null ? json.toString() : null;
    }
} 