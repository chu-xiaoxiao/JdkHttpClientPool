package http;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.util.List;
import java.util.Optional;

/**
 * @author : zhangyuchen
 * @date : 2019/2/13 15:28
 */
public class HttpClientPoolBuilderImpl implements HttpClientPool.Builder {

    /**
     * 连接池大小
     */
    Integer poolSize;
    /**
     * 超时时间
     */
    Long connectTimeout;

    /**
     * 代理
     */
    List<ProxySelector> proxySelectors;


    @Override
    public HttpClientPool.Builder poolSize(Integer poolSize) {
        Optional<Integer> ps = Optional.ofNullable(poolSize);
        this.poolSize = ps.orElse(10);
        return this;
    }

    @Override
    public HttpClientPool.Builder connectTimeout(Long connectTimeout) {
        Optional<Long> cT = Optional.ofNullable(connectTimeout);
        this.connectTimeout = cT.orElse(6000L);
        return this;
    }

    @Override
    public HttpClientPool.Builder proxy(List<String> proxy) {
        proxy.stream().filter(item -> !item.contains(":"))
                .forEachOrdered(item -> this.proxySelectors.add(ProxySelector.of(new InetSocketAddress(item.split(":")[0],Integer.valueOf(item.split(":")[1])))));
        return this;
    }


    @Override
    public HttpClientPool build() {
        return HttpClientPoolImpl.create(this);
    }
}
