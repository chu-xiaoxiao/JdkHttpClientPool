package http.util;

import http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * @author : zhangyuchen
 * @date : 2019/3/6 11:07
 */
@Slf4j
public class HttpUtil {
    public static Document getDocument(String url) {
        Document document = null;
        try {
            log.info("请求地址[{}]",url);
            document = Jsoup.parse(HttpRequest.newRequest()
                    .header(
                            "Access-Control-Allow-Headers","Origin,No-Cache,X-Requested-With,If-Modified-Since,Pragma,Last-Modified,Cache-Control,Expires,Content-Type,Access-Control-Allow-Credentials",
                            "User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.119 Safari/537.36"
                    )
                    .url(url).GetString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return document;
    }
}
