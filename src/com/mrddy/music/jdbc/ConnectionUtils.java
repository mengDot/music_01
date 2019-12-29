package com.mrddy.music.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


/**
 * 为了达到灵活性，我们将jdbc连接的信息公开到properties文件
 * @author  kerwin
 * @version V1.0
 * @time 2019-07-01
 */
public class ConnectionUtils {


    private final JdbcPool jdbcPool;
    //连接信息
    String jdbcDriver;
    String jdbcUrl;
    String username;
    String password;


    private static ConnectionUtils connectionUtils;

    public static ConnectionUtils getInstance(){
        return getInstance("db.properties");
    }


    /**
     * 将当前类单例化
     * 原因，这个类是一个类似与工厂的一个类
     * 他只需要一个
     * @param url
     * @return
     */
    public static ConnectionUtils getInstance(String url){
        if(connectionUtils == null){
            connectionUtils = new ConnectionUtils(url);
        }
        return connectionUtils;
    }


    /**
     * 加载数据
     * 提取数据
     * @param url 文件路径
     */
    private ConnectionUtils(String url){
        jdbcPool = PoolManager.getInstance();
        //1.调用类加载器去对于xx.properteis文件进行加载
        InputStream is = ConnectionUtils.class.getClassLoader().getResourceAsStream(url);

        //构建属性文件对象
        Properties p = new Properties();

        //2.将读进来的文件加载到这个属性对象当中
        try {
            p.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //从属性文件当中取出数据，添加到我们自己的当前类的变量里
        jdbcDriver = p.getProperty("jdbcDriver");
        jdbcUrl = p.getProperty("jdbcUrl");
        username = p.getProperty("username");
        password = p.getProperty("password");

    }


    /**
     * 主动对外提供连接对象
     * @return 连接对象
     */
    public Connection getConnection(){


        return jdbcPool.getConnection().getConn();
    }


    /**
     * 关闭连接工具类
     * @param conn
     * @param s
     * @param rs
     */
    public static void close(Connection conn, Statement s , ResultSet rs){

        if(rs != null){
            try {
                if(rs != null){
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(s != null){
            try {
                if(s != null){
                    s.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(conn!= null ){
            try {
                if(conn != null){
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 关闭连接工具类
     * @param conn
     * @param s
     * @param rs
     */
    public static void close( Statement s , ResultSet rs,PoolConnection conn){

        if(rs != null){
            try {
                if(rs != null){
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(s != null){
            try {
                if(s != null){
                    s.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(conn!= null ){
           conn.setUse(false);
        }


    }

    public static void close(Connection conn){

        close(conn,null,null);


    }



    public static void close(Statement s){


        close(null,s,null);


    }



    public static void close( ResultSet rs){

        close(null,null,rs);



    }






    public static void main(String[] args) {



        Connection connection = ConnectionUtils.getInstance("jdbc.properties").getConnection();
        System.out.println(connection);
    }


}
