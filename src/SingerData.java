import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class SingerData {
    public static void main(String[] args) throws Exception {

        Connection.Response tokenconn = Jsoup.connect("http://www.kuwo.cn/singers").execute();

        String kw_token = tokenconn.cookie("kw_token");

        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            Connection header = Jsoup.connect("http://www.kuwo.cn/api/www/artist/artistInfo?category=0&pn="+i+"&rn=100&reqId=f135a1a0-20fd-11ea-8c43-355f76671211\n")
                    .ignoreContentType(true)
                    .header("csrf",kw_token )
                    .header("Host","http://www.kuwo.cn/singers")
                    .header("Referer","http://www.kuwo.cn/rankList")
                    .cookie("kw_token",kw_token);


            Document document = header.get();
            System.out.println(document.body().text());


            JSONObject obj = new JSONObject(document.text());
            JSONObject data = obj.getJSONObject("data");
            JSONArray artistList = data.getJSONArray("artistList");

            for (int j = 0; j < artistList.length(); j++) {

                System.out.println(artistList.getJSONObject(j).get("name"));
            }
        }


    }
}
