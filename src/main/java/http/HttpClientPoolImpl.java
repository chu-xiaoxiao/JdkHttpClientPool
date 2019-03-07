package http;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : zhangyuchen
 * @date : 2019/2/13 15:43
 */
@Slf4j
public class HttpClientPoolImpl extends HttpClientPool {

    private List<HttpClientItem> httpClients = new ArrayList<>(10);

    private AtomicInteger clientsCount = new AtomicInteger();

    @Override
    public <T> HttpResponse<T> sendGet(http.HttpRequest.Request httpRequest, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
        HttpClientItem httpClientItem = getHttpClient();
        httpClientItem.free = false;
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(URI.create(httpRequest.url))
                .timeout(Duration.ofMillis(600000))
                .GET()
                .build();
        if (httpRequest.headers != null) {
            builder.headers(httpRequest.headers);
        }
        HttpResponse<T> httpResponse =  httpClientItem.getHttpClient().send(builder.build(), responseBodyHandler);
        httpClientItem.free=true;
        return httpResponse;
    }

    @Override
    public <T> HttpResponse<T> sendPost(http.HttpRequest.Request httpRequest, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
        HttpClientItem httpClientItem = getHttpClient();
        httpClientItem.free = false;
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.uri(URI.create(httpRequest.url))
                .timeout(Duration.ofMillis(600000))
                .POST(HttpRequest.BodyPublishers.ofString(httpRequest.body))
                .build();
        if (httpRequest.headers != null) {
            builder.headers(httpRequest.headers);
        }
        HttpResponse<T> httpResponse = httpClientItem.getHttpClient().send(builder.build(), responseBodyHandler);
        httpClientItem.free = true;
        return httpResponse;
    }

    @Override
    protected HttpClientItem getHttpClient() {
        if (clientsCount.addAndGet(1) >= httpClients.size()) {
            clientsCount.set(0);
        }

        while (!httpClients.get(clientsCount.get()).free){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.debug("http连接池[{}]/[{}]", clientsCount.get(), httpClients.size());
        HttpClientItem result = httpClients.get(clientsCount.get());
        return result;
    }


    private static final class SingleFacadeFactory {
        HttpClientPoolFacade facade;

        HttpClientPoolFacade createFacade(HttpClientPoolImpl impl) {
            assert facade == null;
            return (facade = new HttpClientPoolFacade(impl));
        }
    }

    public static HttpClientPoolFacade create(HttpClientPoolBuilderImpl builder) {
        SingleFacadeFactory singleFacadeFactory = new SingleFacadeFactory();
        HttpClientPoolImpl httpClientPool = new HttpClientPoolImpl(builder);
        singleFacadeFactory.facade = singleFacadeFactory.createFacade(httpClientPool);
        return singleFacadeFactory.facade;
    }

    private HttpClientPoolImpl(HttpClientPoolBuilderImpl builder) {
        Integer proxyCount = 0;
        HttpClient.Builder httpclient = HttpClient.newBuilder();
        for (int i = 0; i < builder.poolSize; i++) {
            httpclient.connectTimeout(Duration.ofMillis(builder.connectTimeout))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
            if (!CollectionUtils.isEmpty(builder.proxySelectors)) {
                if (proxyCount >= builder.proxySelectors.size()) {
                    proxyCount = 0;
                }
                httpclient.proxy(builder.proxySelectors.get(proxyCount++));
            }
            httpClients.add(HttpClientItem.defaultOfHttpClient(httpclient.build()));
        }
    }
}
