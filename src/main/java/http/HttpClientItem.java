package http;

import lombok.Data;

import java.net.http.HttpClient;

/**
 * @author : zhangyuchen
 * @date : 2019/3/7 10:55
 */
@Data
class HttpClientItem {
    HttpClient httpClient;
    /**
     * 是否空闲
     */
    Boolean free;

    public static HttpClientItem defaultOfHttpClient(HttpClient httpClient){
        HttpClientItem httpClientItem = new HttpClientItem();
        httpClientItem.httpClient = httpClient;
        httpClientItem.free = true;
        return httpClientItem;
    }
}
