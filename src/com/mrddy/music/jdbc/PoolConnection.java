package com.mrddy.music.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * 连接类
 *
 * @author kerwin
 */
public class PoolConnection {

    /**
     * 数据库连接
     */
    private Connection conn = null;

    /**
     * 标记该连接是否使用
     */
    private boolean isUse = false;

    /*
    * 构造方法
    * */
    public PoolConnection(Connection conn, boolean isUse) {
        this.conn = conn;
        this.isUse = isUse;
    }

    /**
     * 查询实现
     */
    public ResultSet queryBySql(String sql) {
        Statement sm = null;
        ResultSet rs = null;
        try {
            sm = conn.createStatement();
            rs = sm.executeQuery(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public boolean isUse() {
        return isUse;
    }

    public void setUse(boolean use) {
        isUse = use;
    }

    /**
     * 将该连接置为可用状态
     */
    public void close() {
        this.isUse = false;
    }
}
