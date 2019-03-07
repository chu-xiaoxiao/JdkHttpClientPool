package http;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;

/**
 * @author : zhangyuchen
 * @date : 2019/2/12 10:50
 */
@Slf4j
public class HttpRequest {

    private static HttpClientPool httpClientPool = HttpClientPool.newBuilder().connectTimeout(6000000L).poolSize(20).build();

    public static class Request{

        String url;

        String body;

        String[] headers;

        public Request url(String url){
            this.url = url;
            return this;
        }

        public Request body(String body){
            this.body = body;
            return this;
        }

        public Request header(String... headers){
            this.headers = headers;
            return this;
        }


        public <T>HttpResponse<T> Get(HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
            return httpClientPool.sendGet(this, responseBodyHandler);
        }

        public <T>HttpResponse<T> Post(HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
            return httpClientPool.sendPost(this,responseBodyHandler);
        }

        public String GetString() throws IOException, InterruptedException {
           return httpClientPool.sendGet(this, HttpResponse.BodyHandlers.ofString(Charset.forName("UTF-8"))).body();
        }

        public String PostString() throws IOException, InterruptedException {
            return httpClientPool.sendPost(this, HttpResponse.BodyHandlers.ofString(Charset.forName("UTF-8"))).body();
        }

    }

    public static Request newRequest(){
        return new Request();
    }

}
