package com.mrddy.music.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;


/**
 * 简化jdbc操作，并提高jdbc灵活性
 */
public class JDBCUtil {

    Connection conn;
    Statement s;
    PreparedStatement ps;
    ResultSet rs;
    JdbcPool pool;


    public JDBCUtil(){
        pool = PoolManager.getInstance();
    }


    /**
     * 给执行对象设置参数
     * @param conn
     * @param sql
     * @param params
     */
    public void createStatementParams(Connection conn,String sql,List<Object> params){
        //2.取得一个预编译执行对象
        try {
            ps = conn.prepareStatement(sql);

            //如果参数不为空，就设置参数
            if(params != null){
                //参数匹配，就是去替代语句当中的?
                //insert into tb_users(username,userpwd) values(?,?)
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject((i+1),params.get(i));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /**
     * 为了满足易用性
     * 给我一条sql， 我直接执行增删改操作
     * @param sql 要执行的sql
     * @param params 这个sql里面是否要有参数 没有参数给null值  例子：insert into users(name) values(?)
     * @return 受影响行数
     */
    public int executeUpdate(String sql, List<Object> params){


        //要返回出去的结果
        int result = 0;
        try {
            //1.想要获取连接，直接找ConnectionUtils
            Connection conn = pool.getConnection().getConn();
            //2.给执行对象设置参数
            createStatementParams(conn,sql,params);

            //执行这条语句
            result = ps.executeUpdate();


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //不管是否有异常出现，我都进行关闭连接
            ConnectionUtils.close(conn,ps,rs);

        }

        return result;
    }

    /**
     * 批处理语句
     * 一次性执行多条sql
     * @param sql
     * @return
     */
    public int executeUpdate(String... sql){
        //批量增加
        PoolConnection con = pool.getConnection();
        int result = 0;
        try {
            Connection conn = con.getConn();
            //只有statement这个对象支持批处理
            s = conn.createStatement();


            for (int i = 0; i < sql.length; i++) {
                //将语句加入到批处理当中，这里并不会去访问数据库
                s.addBatch(sql[i]);
            }

            //批量发送语句
            int[] ints = s.executeBatch();
            for (int i = 0; i < ints.length; i++) {
                result += ints[i];
            }

        }catch (Exception e){
            System.out.println("录入数据失败："+e.getMessage());
        }finally {
            ConnectionUtils.close(ps,rs,con);

        }

        return result;
    }


    public Map<String,Object> getResult(){
        //每一行记录，都封装成一个map提供给人使用
        Map<String,Object> obj = new HashMap<String,Object>();

        try {
            //封装成map
            //rs.getMetaData().getColumnCount() 获得数据结果的列长度
            for (int i = 0; i < rs.getMetaData().getColumnCount() ; i++) {
                //获得数据结果的列名
                String key = rs.getMetaData().getColumnLabel(i+1);
                //获得当前列的值
                Object value = rs.getObject(i+1);
                //往map中封装数据
                obj.put(key,value);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return obj;
    }


    /**
     * //2.给我一条sql， 我直接查询操作并且查询回来的数据是组装好的
     * @param sql 要查询的sql
     * @param params 要执行的sql中需要的参数
     * @return 结果是MAP类型
     */
    public List<Map<String,Object>> executeQuery(String sql, List<Object> params){

        //要返回出去的结果
        List<Map<String,Object>> data = null;
        try {
            //建立连接
            Connection conn = pool.getConnection().getConn();

            createStatementParams(conn,sql,params);
            //执行语句，获得返回结果
            rs = ps.executeQuery();

            //构建最终要返回出去的list列表
            data = new ArrayList<Map<String,Object>>();

            //迭代整个集合里面的数据
            while (rs.next()){

                //将数据封装成一个map
                Map<String,Object> obj = getResult();
                //将一条记录放入到最终的列表当中
                data.add(obj);

            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ConnectionUtils.close(conn,ps,rs);

        }
        return data;
    }


    /**
     * 给一条sql,直接查询出来一个列表，这个列表数据是我们自己指定的数据类型
     * @param sql 要执行的sql
     * @param params 参数
     * @param clazz 我们指定的类型
     * @param <T> 我们想要的类型
     * @return 自己指定类型的一个列表数据
     */
    public <T> List<T> executeQuery(String sql, List<Object> params,Class<T> clazz){

        List<T> data = null;
        try {
            Connection conn = pool.getConnection().getConn();
            ps = conn.prepareStatement(sql);

            if(params != null){
                //参数匹配
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject((i+1),params.get(i));
                }

            }
            rs = ps.executeQuery();

            data = new ArrayList<T>();

            while (rs.next()){
                Map<String,Object> obj = new HashMap<String,Object>();


                for (int i = 0; i < rs.getMetaData().getColumnCount() ; i++) {
                    String key = rs.getMetaData().getColumnLabel(i+1);
                    Object value = rs.getObject(i+1);

                    obj.put(key,value);
                }

                //将map自动封装成一个指定的对象
                T t = populate(clazz, obj);

                data.add(t);

            }


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ConnectionUtils.close(conn,ps,rs);

        }
        return data;
    }




    //将一个map转换成一个属性


    /**
     * 将一个Map封装成一个指定的对象
     * @param clazz 要封装成什么对象
     * @param map map 数据
     * @param <T> 指定的类型
     * @return 指定类型数据对象
     * @throws Exception
     */
    public <T> T populate(Class<T> clazz,Map<String,Object> map) throws Exception {

        //1.先构建一个对象
        T t = clazz.newInstance();



        //获得所有的map的key
        Set<String> set = map.keySet();
        Iterator<String> iterator = set.iterator();
        //迭代key
        while(iterator.hasNext()){
            //获得key的具体值
            String next = iterator.next();

            try{
                //用key匹配对象当中的属性
                Field name = clazz.getDeclaredField(next);

                //不用！
//                name.setAccessible(true);
//                name.set(t,map.get(next));


//                真正的赋值方案：调用set方法
                //拼接出一个set方法
                String methodName = "set";
                String fieldName = name.getName().substring(0,1).toUpperCase()+ name.getName().substring(1);
                methodName = methodName + fieldName;


                //在当前对象中获得setXX方法
                Method m = clazz.getDeclaredMethod(methodName, name.getType());


                //执行方法
                m.invoke(t,map.get(next));



            }catch (NoSuchFieldException e){
                System.out.println("当前数据项"+e.getMessage()+"不匹配属性项！跳过！");
            }

        }


        return t;
    }

    /***
     * 给我一个对象，我直接录入到数据库当中
     * @param obj
     * @return
     */
    public int add(Object... obj){
        int result = 0;

        //获取整个数组中的对象，并生成对应的sql
        //sqls作为sql容器
        String[] sqls = new String[obj.length];

        for (int k = 0; k < obj.length; k++) {

//      设定：由java程序当中的一个pojo对应数据库里面的一张表
            //pojo = javabean
            //一个pojo 对象 对象应一条数据库表数据
            Class clazz = obj[k].getClass();

            //1.拼接sql
            StringBuffer sql = new StringBuffer("insert into ");

            //1.1 提取表名
            String tablename = "";


            //提取注解中信息
            Table table = (Table) clazz.getAnnotation(Table.class);
            if(table != null){
                //提取注解内容
                tablename = table.tableName();
            }else{
                throw new NotFoundTalbeException();
            }
            /* end if(table != null) */


            sql.append(tablename+"(");

            //1.2.提取字段
            Field[] fs = clazz.getDeclaredFields();

            for (int i = 0; i < fs.length; i++) {
                Field f = fs[i];
                //取字段上的注解是需要从Field这个类去拿
                //取类上面的注解需要从Class

                //这两个注解是用来区分自增长模式
                Primary primary = f.getAnnotation(Primary.class);
                AutoIncrement auto = f.getAnnotation(AutoIncrement.class);

                //自增长模式不需要在新增的时候使用
                if(primary == null && auto == null){
                    Column column = f.getAnnotation(Column.class);
                    String name = column.value();
                    sql.append(name+",");
                }



            }

            sql.delete(sql.length()-1,sql.length());
            sql.append(") values(");


            //拼value
            for (int i = 0; i < fs.length; i++) {


                Field f = fs[i];
                Primary primary = f.getAnnotation(Primary.class);
                AutoIncrement auto = f.getAnnotation(AutoIncrement.class);

                if(primary == null && auto == null) {
                    String name = fs[i].getName();

                    //通过调用get方法取数据
                    String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                    try {
                        Method method = clazz.getDeclaredMethod(methodName, null);
                        Object value = method.invoke(obj[k], null);
                        //判断每个类型，因为每个类型在sql中的表现形式不一样
                        if (fs[i].getType() == Integer.class || fs[i].getType() == Integer.TYPE) {
                            sql.append(value + ",");
                        } else if (String.class == fs[i].getType()) {
                            sql.append("'" + value + "',");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            sql.delete(sql.length()-1,sql.length());
            sql.append(") ");



            //2.调用jdbc流程

            sqls[k] = sql.toString();


        }

        result = executeUpdate(sqls);


        return result;
    }









}
