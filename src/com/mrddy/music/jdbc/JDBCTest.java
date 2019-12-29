package com.mrddy.music.jdbc;

public class JDBCTest {

    public static void main(String[] args) throws ClassNotFoundException {
        JDBCUtils utils = new JDBCUtils();
        String sql ="insert into tb_singer(artistFans,albumNum,mvNum,musicNum,pic,pic70,pic120,pic300,aartist,singername,id) values(0,0,0,0,'','','','','','',0);";
        int i = utils.executeUpdate(sql);
        System.out.println(i);


    }

}
