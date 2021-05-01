package com.wjh;

import com.wjh.connection.ConnectionPool;
import com.wjh.connection.DataSource;
import com.wjh.connection.selectStrategy.RandomSelectStrategy;
import com.wjh.connection.selectStrategy.SelectStrategy;
import com.wjh.util.ResultSetUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

/**
 * 入口
 */
public class JdbcTemplate {
    private ConnectionPool connectionPool;

    public JdbcTemplate(DataSource dataSource) {
        this(dataSource, new RandomSelectStrategy());
    }

    public JdbcTemplate(DataSource dataSource, SelectStrategy selectStrategy) {
        this.connectionPool = new ConnectionPool(dataSource, selectStrategy);
    }


    /**
     * 读（查询）
     * 不抛出异常，如果查询失败则返回空list（空list不是null）
     *
     * @param sql 如 select name myName，company from user a where a.id > ?
     */
    public List<Map<String, Object>> query(String sql, Object... params) throws Exception {
        Connection connection = connectionPool.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        // 设置参数到sql中
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        ResultSet resultSet = ps.executeQuery();
        return ResultSetUtil.resultSetToList(resultSet);
    }

    /**
     * 写（增删改）
     */
    public Integer update(String sql, Object... params) throws Exception {
        return update(sql, connectionPool.getConnection(), params);
    }

    /**
     * 写（增删改）
     *
     * @param connection 用于实现事务
     */
    public Integer update(String sql, Connection connection, Object... params) throws Exception {
        PreparedStatement ps = connection.prepareStatement(sql);
        // 设置参数到sql中
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        return ps.executeUpdate();
    }
}