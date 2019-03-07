package http;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * @author : zhangyuchen
 * @date : 2019/2/13 15:28
 */
public abstract class HttpClientPool {


    public static HttpClientPool.Builder newBuilder() {
        return new HttpClientPoolBuilderImpl();
    }
    public interface Builder{

        /**
         * 连接池大小
         */
        Builder poolSize(Integer poolSize);

        /**
         * 超时时间
         */
        Builder connectTimeout(Long connectTimeout);

        /**
         * 代理设置
         * @param proxy like 127.0.0.1:8080
         * @return
         */
        Builder proxy(List<String> proxy);
        /**
         * 创建连接池
         * @return
         */
        HttpClientPool build();


    }

    /**
     * 发送get方法 获取请求
     * @return
     * @param httpRequest
     */
    public abstract <T>HttpResponse<T> sendGet(HttpRequest.Request httpRequest, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException;

    /**
     * 发送Post方法获取请求
     * @return
     * @param httpRequest
     */
    public abstract  <T> HttpResponse<T> sendPost(HttpRequest.Request httpRequest, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException;


    /**
     * 从池中获取HttpClient
     * @return
     */
    protected abstract HttpClientItem getHttpClient();
}
