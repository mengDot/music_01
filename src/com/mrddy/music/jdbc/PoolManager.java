package com.mrddy.music.jdbc;

/**
 * @author kerwin
 */
public class PoolManager {

    /**
    * 静态内部类实现连接池的单例
    * */
    private static class CreatePool{
        private static JdbcPool pool = new JdbcPool();
    }

    public static JdbcPool getInstance(){
        return CreatePool.pool;
    }

}
