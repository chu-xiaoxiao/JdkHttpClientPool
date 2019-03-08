import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import http.HttpRequest;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author : zhangyuchen
 * @date : 2019/2/13 16:39
 */
public class HttpTest {

    public static void main(String[] args) {
        try {
            System.out.println(HttpRequest.newDefaultRequest().url("http://www.baidu.com")
                    .Get(HttpResponse.BodyHandlers.ofByteArray()));
            System.out.println(HttpRequest.newRequest().url("http://www.baidu.com")
                    .GetString());
            System.out.println(HttpRequest.newRequest().url("http://www.baidu.com")
                    .headers("Access-Control-Allow-Headers","Origin,No-Cache,X-Requested-With,If-Modified-Since,Pragma,Last-Modified,Cache-Control,Expires,Content-Type,Access-Control-Allow-Credentials",
                            "User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.119 Safari/537.36")
                    .body("body")
                    .Post(HttpResponse.BodyHandlers.ofByteArray()));
            System.out.println(HttpRequest.newRequest().url("http://www.baidu.com")
                    .body("body")
                    .PostString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

   /* public static void main(String[] args) {
        var jsonArray = new JSONArray();
        try {
            var lines = Files.lines(Paths.get("/Users/zhangyuchen/intellij/bilibili/src/main/resources/1.txt"));
            lines.map(line -> {
                        var jsonObject = new JSONObject();
                        jsonObject.put("sort", "");
                        jsonObject.put("field2", "");
                        jsonObject.put("fieldName", "indicator");
                        jsonObject.put("fieldType", "indicator");
                        jsonObject.put("fieldValue", line.split(" ")[0]);
                        jsonObject.put("filterSymbol", "");
                        return jsonObject;
                    }
            ).forEach(jsonArray::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String jsonResult = jsonArray.toJSONString();
        System.out.println(jsonResult.substring(1,jsonResult.length()-1));

    }*/

}
