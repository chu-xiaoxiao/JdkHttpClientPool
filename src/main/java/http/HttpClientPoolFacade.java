package http;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * @author : zhangyuchen
 * @date : 2019/2/13 16:00
 */
@Slf4j
public class HttpClientPoolFacade extends HttpClientPool{

    final HttpClientPoolImpl impl;



    public HttpClientPoolFacade(HttpClientPoolImpl impl){
        this.impl = impl;
    }

    @Override
    public <T> HttpResponse<T> sendGet(HttpRequest.Request httpRequest, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        HttpResponse result =  impl.sendGet(httpRequest,responseBodyHandler);
        log.debug("请求地址[{}]请求时间[{}ms]",httpRequest,System.currentTimeMillis()-start);
        return result;
    }

    @Override
    public <T> HttpResponse<T> sendPost(HttpRequest.Request httpRequest, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        HttpResponse result =  impl.sendGet(httpRequest,responseBodyHandler);
        log.debug("请求地址[{}]请求时间[{}ms]",httpRequest,System.currentTimeMillis()-start);
        return result;
    }

    @Override
    protected HttpClientItem getHttpClient() {
        return impl.getHttpClient();
    }
}
