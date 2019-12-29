package com.mrddy.music.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

public class JDBCUtils {
     Connection conn;
     Statement s;
     PreparedStatement ps;
     ResultSet rs;

    /**
     * 使用占位符并传入参数完成增删改
     * @param sql
     * @param param
     * @return
     */
    public int executeUpdate(String sql, List<Object> param){
        int result=0;
        try {
            Connection conn=ConnectionUtils.getInstance().getConnection();
            ps=conn.prepareStatement(sql);
            if (param!=null){
                for(int i=0;i<param.size();i++){
                    ps.setObject((i+1),param.get(i));
                }
            }
            result=ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //finally：不管是否发生异常都会执行
            ConnectionUtils.close(conn,ps,rs);
        }
        return result;
    }

    /**
     * 批量插入
     * @param sql
     * @return
     */
    public int executeUpdate(String ...sql){
        int result=0;
        try {
            Connection conn=ConnectionUtils.getInstance().getConnection();
            //只有Statement对象支持批处理
            s=conn.createStatement();
            for (int i=0;i<sql.length;i++){
                s.addBatch(sql[i]);
            }
            s.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionUtils.close(conn,ps,rs);
        }
        return result;
    }

    /**
     *
     * @param sql
     * @param params 要执行的sql中需要的参数
     * @return 返回的是Map列表类型
     */
    public List<Map<String,Object>> executeQuery(String sql, List<Object> params){
        List<Map<String,Object>> data=null;
        try {
            Connection conn=ConnectionUtils.getInstance().getConnection();
            ps=conn.prepareStatement(sql);
            if(params!=null){
                for (int i=0;i<params.size();i++){
                    ps.setObject((i+1),params.get(1));
                }
            }
            rs=ps.executeQuery();
            data=new ArrayList<>();
            while (rs.next()){
                Map<String,Object>obj=new HashMap<>();
                //rs.getMetaData().getColumnCount() 获得查询语句数据结果的列长度
                for (int i=0;i<rs.getMetaData().getColumnCount();i++){
                    //getColumnLabel获取查询语句返回的列名
                    String key=rs.getMetaData().getColumnLabel(i+1);
                    //获取当前列的数值
                    Object value=rs.getObject(i+1);
                    obj.put(key,value);
                }
                data.add(obj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionUtils.close(conn,ps,rs);
        }
        return data;
    }

    /**
     *
     * @param sql 使用占位符
     * @param params 参数
     * @param clazz 指定的类
     * @param <T>
     * @return 返回指定的列表数据
     */
    public <T> List<T> executeQuery(String sql,List<Object> params,Class<T> clazz){
        List<T> data=null;
        try {
            Connection conn=ConnectionUtils.getInstance().getConnection();
            ps=conn.prepareStatement(sql);
            if(params!=null){
                for(int i=0;i<params.size();i++){
                    ps.setObject((i+1),params.get(i));
                }
            }
            rs=ps.executeQuery();
            data=new ArrayList<>();
            while (rs.next()){
                Map<String,Object> obj=new HashMap<>();
                for (int i=0;i<rs.getMetaData().getColumnCount();i++){
                    String key=rs.getMetaData().getColumnLabel(i+1);
                    Object value=rs.getObject(i+1);
                    obj.put(key,value);
                }
                //将map封装成一个指定对象
                T t=populate(clazz,obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConnectionUtils.close(conn,ps,rs);
        }
        return data;
    }

    /**
     * 将一个Map封装成一个指定的对象
     * @param clazz 封装的对象
     * @param map 数据
     * @param <T> 指定的类型
     * @return 返回一个想要的对象
     * @throws Exception
     */
    public <T> T populate(Class<T> clazz,Map<String,Object>map)throws Exception{
        T t=clazz.newInstance();
        //获取map的所有key
        Set<String> set=map.keySet();
        //迭代所有key
        Iterator<String> iterator=set.iterator();
        while (iterator.hasNext()){
            String next=iterator.next();
            try {
                //用key匹配对象中的属性
                Field name=clazz.getDeclaredField(next);
                String methodName="set";
                //将属性名首字母大写
                String fildName=name.getName().substring(0,1).toUpperCase()+name.getName().substring(1);
                //形如setXxx
                methodName=methodName+fildName;
                //在当前对象中获得setXxx方法
                Method m=clazz.getDeclaredMethod(methodName,name.getType());
                m.invoke(t,map.get(next));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        return t;
    }

    /**
     * 给多个对象，实现批量直接录入数据库
     * @param obj
     * @return
     */
    public int addAll(Object...obj){
        int result=0;
        String[] sqls=new String[obj.length];
        for(int k=0;k<obj.length;k++){
            Class clazz=obj[k].getClass();
            StringBuffer sql=new StringBuffer("insert into");
            //存储表名
            String tablename="";
            Table table=(Table)clazz.getAnnotation(Table.class);
            if(table!=null){
                tablename=table.tableName();
            }else {
                throw new NotFoundTableException();
            }
            sql.append(tablename+"(");
            //提取字段
            Field[] fs=clazz.getDeclaredFields();//获取类的属性名
            for (int i=0;i<fs.length;i++){
                Field f=fs[i];
                //这两个注解区分自增长,获取属性上的注解
                Primary primary=f.getAnnotation(Primary.class);
                AutoIncrement auto=f.getAnnotation(AutoIncrement.class);
                if(primary==null&&auto==null){
                    Column column=f.getAnnotation(Column.class);
                    String name=column.value();
                    sql.append(name+",");
                }
            }
            sql.delete(sql.length()-1,sql.length());
            sql.append(") values(");
            //拼values部分
            for(int i=0;i<fs.length;i++){
                Field f=fs[i];
                Primary primary=f.getAnnotation(Primary.class);
                AutoIncrement auto=f.getAnnotation(AutoIncrement.class);
                if(primary==null&&auto==null){
                    String name=fs[i].getName();
                    //形如getXxx
                    try {
                        String methodName="get"+name.substring(0,1).toUpperCase()+name.substring(1);
                        Method method=clazz.getDeclaredMethod(methodName,null);
                        Object value = method.invoke(obj[k], null);
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
            System.out.println(sql.toString());


            //2.调用jdbc流程

            sqls[k] = sql.toString();
        }
        result=executeUpdate(sqls);
        return result;
    }
}
