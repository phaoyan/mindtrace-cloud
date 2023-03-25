package pers.juumii.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

public class MybatisDurationTypeHandler extends BaseTypeHandler<Duration> {
    @Override
    public void setNonNullParameter(
            PreparedStatement preparedStatement,
            int i,
            Duration duration,
            JdbcType jdbcType)
            throws SQLException {

    }

    @Override
    public Duration getNullableResult(
            ResultSet resultSet,
            String s)
            throws SQLException {
        return null;
    }

    @Override
    public Duration getNullableResult(
            ResultSet resultSet,
            int i)
            throws SQLException {
        return null;
    }

    @Override
    public Duration getNullableResult(
            CallableStatement callableStatement,
            int i)
            throws SQLException {
        return null;
    }
}
