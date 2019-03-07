import http.HttpRequest;

import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * @author : zhangyuchen
 * @date : 2019/2/13 16:39
 */
public class HttpTest {

    public static void main(String[] args) {
        try {
            System.out.println(new HttpRequest().newRequest().url("http://www.baidu.com").Get(HttpResponse.BodyHandlers.ofByteArray()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

/*    public static void main(String[] args) {
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
        System.out.println(jsonArray.toJSONString());

    }*/

}
