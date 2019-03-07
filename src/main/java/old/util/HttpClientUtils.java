package old.util;

import lombok.extern.slf4j.Slf4j;
import old.bean.GNIP;
import old.get.GetIP;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author : zhangyuchen
 * @date : 2018/11/28 10:41
 */
@Slf4j
public class HttpClientUtils {
    private static PoolingHttpClientConnectionManager cm = null;
    
    /**
     * 请求代理
     */
    private HttpHost proxy = null;

    static{
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10).build();
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(400);
        cm.setDefaultMaxPerRoute(50);
    }

    public HttpHost getProxy() {
        return proxy;
    }

    public void setProxy(HttpHost proxy) {
        this.proxy = proxy;
    }

    public CloseableHttpClient getHttpClient() {
        RequestConfig globalConfig = RequestConfig.custom().setConnectTimeout(10000).setCookieSpec(CookieSpecs.DEFAULT).build();
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"));
        headers.add(new BasicHeader("Accept-Encoding","gzip, deflate, br"));
        headers.add(new BasicHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36"));

        CloseableHttpClient client = HttpClients.custom()
                .setDefaultHeaders(headers)
                .setConnectionManager(cm)
                .setProxy(proxy)
                .setDefaultRequestConfig(globalConfig)
                .build();

        return client;
    }


    public Document getDocument(String url) throws IOException {
        log.info("页面地址[{}] 代理IP[{}]",url,this.proxy);
        HttpGet httpGet = new HttpGet(url);
        String response = null;
        response = EntityUtils.toString(getHttpClient().execute(httpGet).getEntity(),"UTF-8");
        Document document = Jsoup.parse(response);
        return document;
    }

    public Document getDocumentWinTime(String url,Long ms) throws IOException {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getDocument(url);
    }

    public Document getDocumentWithIP (String url){
        Document document1 = null;
        Random random = new Random(System.currentTimeMillis());
        List<GNIP> gnips = null;
        try {
            gnips = GetIP.getIP(getDocument("https://www.xicidaili.com/nn/"+random.nextInt(3000)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(document1 == null){
            GNIP gnip = gnips.get(random.nextInt(gnips.size()));
            this.setProxy(new HttpHost(gnip.getIp(),Integer.valueOf(gnip.getPort())));
            try{
                document1 =  getDocument(url);
            }catch (Exception e ){
                log.warn("超时 重试");
            }
        }
        return document1;
    }
}
