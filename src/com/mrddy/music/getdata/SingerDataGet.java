package com.mrddy.music.getdata;

import com.mrddy.music.jdbc.JDBCUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SingerDataGet {


    public static void main(String[] args) throws Exception {
        //1.第一次访问，先拿到cookie
        Connection.Response response = Jsoup.connect("http://www.kuwo.cn/singers").execute();

        //2.取得cookie数据
        String kw_token = response.cookie("kw_token");
        System.out.println("token:"+kw_token);

        for (int j = 1; j <= 220; j++) {


            Thread.sleep(2000);


            Connection conn = Jsoup.connect("http://www.kuwo.cn/api/www/artist/artistInfo?category=0&pn="+j+"&rn=100&reqId=06b53d00-212c-11ea-9c64-89f7f37d7421")
                    .ignoreContentType(true)
                    .header("csrf", kw_token)
                    .header("Referer", "http://www.kuwo.cn/rankList")
                    .cookie("kw_token",kw_token);

            //3.进行连接get
            Document document = conn.get();
            //访问之后的结果形成一个文本数据
            String text = document.text();

            System.out.println(text);

            JSONObject obj = new JSONObject(text);

            JSONObject data = obj.getJSONObject("data");
            JSONArray artistList = data.getJSONArray("artistList");
            for (int i = 0; i < artistList.length(); i++) {
                Thread.sleep(500);
                JSONObject singerdata = artistList.getJSONObject(i);


                int id = singerdata.getInt("id");
                int artistFans = singerdata.getInt("artistFans");
                int albumNum = singerdata.getInt("albumNum");
                int mvNum = singerdata.getInt("mvNum");
                int musicNum = singerdata.getInt("musicNum");
                int isStar = singerdata.getInt("isStar");
                int content_type = singerdata.getInt("content_type");

                String pic = singerdata.getString("pic");
                String pic300 = singerdata.getString("pic300");
                String pic120 = singerdata.getString("pic120");
                String pic70 = singerdata.getString("pic70");


                String aartist = singerdata.getString("aartist");
                String name = singerdata.getString("name");

                JDBCUtils utils = new JDBCUtils();
                String sql ="insert into tb_singer(artistFans,albumNum,mvNum,musicNum,pic,pic70,pic120,pic300,aartist,singername,id) " +
                        "values("+artistFans+","+albumNum+","+mvNum+","+musicNum+",'"+pic+"','"+pic70+"','"+pic120+"','"+pic300+"','"+aartist+"','"+name+"',"+id+");";
                System.out.println(sql);
                utils.executeUpdate(sql);





//                getMusicData(id,musicNum);


            }
        }
    }



    public static void getMusicData(int singerid,int total) throws IOException {
        //1.第一次访问，先拿到cookie
        Connection.Response response = Jsoup.connect("http://www.kuwo.cn/singer_detail/"+singerid).execute();

        //2.取得cookie数据
        String kw_token = response.cookie("kw_token");
        System.out.println("token:"+kw_token);

        //获取总歌曲数量
        int pagecount = total % 30 == 0 ? total / 30 : (total / 30) + 1;

        for (int j = 1; j <= pagecount; j++) {
            Connection conn = Jsoup.connect("http://www.kuwo.cn/api/www/artist/artistMusic?artistid="+singerid+"&pn="+j+"&rn=30&reqId=1e8801e0-2133-11ea-9c64-89f7f37d7421\n")
                    .ignoreContentType(true)
                    .header("csrf", kw_token)
                    .header("Referer", "http://www.kuwo.cn/singer_detail/"+singerid)
                    .cookie("kw_token",kw_token);





            //3.进行连接get
            Document document = conn.get();
            //访问之后的结果形成一个文本数据
            String text = document.text();

            System.out.println(text);

            JSONObject obj = new JSONObject(text);
            JSONObject data = obj.getJSONObject("data");
            JSONArray list = data.getJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                //在这里已经爬去到了所有的歌手数据
                JSONObject music = list.getJSONObject(i);
                int rid = music.getInt("rid");




                getMusic(rid);
                getMusicText(rid);
            }
        }

    }




    public static void getMusic(int rid) throws IOException {
        Connection connect = Jsoup.connect("http://www.kuwo.cn/url?format=mp3&rid=" + rid + "&response=url&type=convert_url3&br=128kmp3&from=web&t=1576631457979&reqId=3f736fc1-2133-11ea-9c64-89f7f37d7421");
        Document document = connect.get();

        String text = document.text();

        JSONObject obj = new JSONObject(text);
        String url = obj.getString("url");
        System.out.println(url);
        getDownloadMusic(url,rid);
        //"/Users/xieqingyi/Desktop/musicdownload/"+rid+".mp3"





    }


    public static void getMusicText(int rid) throws IOException {
        Connection connect = Jsoup.connect("http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId="+rid+"&reqId=ec295420-213b-11ea-9c64-89f7f37d7421");
        Document document = connect.get();

        String text = document.text();
        System.out.println(text);






    }

    public static void getDownloadMusic(String url,int rid) throws IOException {
        URL uri = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.connect();

        InputStream input = conn.getInputStream();
        byte[] bytes = new byte[1048576];

        FileOutputStream out = new FileOutputStream("/Users/xieqingyi/Desktop/musicdownload/"+rid+".mp3");
        int len = 0;
        while((len = input.read(bytes))> 0 ){
            out.write(bytes,0,len);
            out.flush();
        }
        out.close();







    }




}
