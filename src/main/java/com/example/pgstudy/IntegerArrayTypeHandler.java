package com.example.pgstudy;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.*;

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

    @Override
    public Integer[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Array array = rs.getArray(columnIndex);
        if (array == null) {
            return null;
        }
        return (Integer[]) array.getArray();
    }

    @Override
    public Integer[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Array array = cs.getArray(columnIndex);
        if (array == null) {
            return null;
        }
        return (Integer[]) array.getArray();
    }
} 