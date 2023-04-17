package pers.juumii.config;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import pers.juumii.utils.DataUtils;
import pers.juumii.utils.TimeUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class TimeListTypeHandler implements TypeHandler<List<LocalDateTime>> {
    @Override
    public void setParameter(
            PreparedStatement preparedStatement,
            int i,
            List<LocalDateTime> o,
            JdbcType jdbcType) throws SQLException {
        String s = JSONUtil.toJsonStr(DataUtils.destructureAll(o, time->time.format(TimeUtils.DEFAULT_DATE_TIME_FORMATTER)));
        preparedStatement.setString(i, s);
    }

    @Override
    public List<LocalDateTime> getResult(
            ResultSet resultSet,
            String s) throws SQLException {
        List<String> timeStrings = JSONUtil.toList(resultSet.getString(s), String.class);
        return DataUtils.destructureAll(timeStrings, str -> LocalDateTime.parse(str, TimeUtils.DEFAULT_DATE_TIME_FORMATTER));
    }

    @Override
    public List<LocalDateTime> getResult(
            ResultSet resultSet,
            int i) throws SQLException {
        List<String> timeStrings = JSONUtil.toList(resultSet.getString(i), String.class);
        return timeStrings.stream().map(str -> LocalDateTime.parse(str, TimeUtils.DEFAULT_DATE_TIME_FORMATTER)).toList();
    }

    @Override
    public List<LocalDateTime> getResult(
            CallableStatement callableStatement,
            int i) throws SQLException {
        List<String> timeStrings = JSONUtil.toList(callableStatement.getString(i), String.class);
        return timeStrings.stream().map(str -> LocalDateTime.parse(str, TimeUtils.DEFAULT_DATE_TIME_FORMATTER)).toList();
    }
}
