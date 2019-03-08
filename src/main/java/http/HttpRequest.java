package http;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.assertj.core.util.Lists;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

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

        List<String> headers = Lists.newArrayList();


        public Request url(String url){
            this.url = url;
            return this;
        }

        public Request body(String body){
            this.body = body;
            return this;
        }

        public Request headers(String... headers){
            this.headers.addAll(Arrays.asList(headers));
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

    public static Request newDefaultRequest(){
        Request request = new Request();
        request.headers(
                "Access-Control-Allow-Headers", "Origin,No-Cache,X-Requested-With,If-Modified-Since,Pragma,Last-Modified,Cache-Control,Expires,Content-Type,Access-Control-Allow-Credentials",
                "User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.119 Safari/537.36"
        );
        return request;
    }

}
