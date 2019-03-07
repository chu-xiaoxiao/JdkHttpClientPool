package bilibili;

import http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import old.util.HttpClientUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.springframework.util.CollectionUtils;
import thread.DownloadThreadFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * 说明
 * 利用httpclient下载文件
 * maven依赖
 * <dependency>
 * <groupId>org.apache.httpcomponents</groupId>
 * <artifactId>httpclient</artifactId>
 * <version>4.0.1</version>
 * </dependency>
 * 可下载http文件、图片、压缩文件
 * bug：获取response header中Content-Disposition中filename中文乱码问题
 *
 * @author tanjundong
 */
@Slf4j
public class HttpDownload {


    private static final Queue<DownloadPath> DOWNLOADPATHS = new LinkedBlockingDeque<>();

    private static final int cache = 10 * 1024;
    private static final boolean isWindows;
    private static final String splash;
    private static final String root;
    private static final HttpClientUtils HTTP_CLIENT_UTILS = new HttpClientUtils();

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(6, 10, 10, TimeUnit.MINUTES, new LinkedBlockingDeque<>(),new DownloadThreadFactory("下载线程"));

    static {
        if (System.getProperty("os.name") != null && System.getProperty("os.name").toLowerCase().contains("windows")) {
            isWindows = true;
            splash = "\\";
            root = "D:";
        } else {
            isWindows = false;
            splash = "/";
            root = "/search";
        }
        Stream.iterate(0, item -> ++item).limit(5).forEach(item -> threadPoolExecutor.execute(new DownloadThread()));
    }

    /**
     * 根据url下载文件，文件名从response header头中获取
     *
     * @param url
     * @return
     */
    public String download(String url) {
        return download(url, null);
    }

    /**
     * 根据url下载文件，保存到filepath中
     *
     * @param url
     * @param filepath
     * @return
     */
    public static String download(String url, String filepath) {
        DOWNLOADPATHS.add(new DownloadPath(url, filepath));
        //threadPoolExecutor.execute();
        return "";
    }

    static class DownloadThread implements Runnable {

        volatile Boolean done = true;

        public DownloadThread() {

        }

        @Override
        public void run() {
            for (; ; ) {
                try {
                    while (!this.done || CollectionUtils.isEmpty(DOWNLOADPATHS)) {
                        Thread.sleep(100);
                    }
                    DownloadPath downloadPath = DOWNLOADPATHS.remove();
                    this.done = false;
                    Path savePath = Path.of(downloadPath.path);
                    Files.createDirectories(savePath.getParent());
                    log.info("文件写入位置 {} 下载路径{}", savePath, downloadPath.url);
                    HttpRequest.newRequest().url(downloadPath.url)
                            .header(
                                    "Access-Control-Allow-Headers", "Origin,No-Cache,X-Requested-With,If-Modified-Since,Pragma,Last-Modified,Cache-Control,Expires,Content-Type,Access-Control-Allow-Credentials",
                                    "User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.119 Safari/537.36"
                            ).Get(java.net.http.HttpResponse.BodyHandlers.ofFile(savePath));
                    NumberFormat percent = NumberFormat.getNumberInstance();
                    percent.setMaximumFractionDigits(2);
                    log.info("写入成功 文件名称 [{}] 文件大小[{}M]", savePath.getFileName(), percent.format(BigDecimal.valueOf(Files.size(savePath)).divide(BigDecimal.valueOf(1000000))));
                    this.done = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取response要下载的文件的默认路径
     *
     * @param response
     * @return
     */
    public static String getFilePath(HttpResponse response) {
        String filepath = root + splash;
        String filename = getFileName(response);

        if (filename != null) {
            filepath += filename;
        } else {
            filepath += getRandomFileName();
        }
        return filepath;
    }

    /**
     * 获取response header中Content-Disposition中的filename值
     *
     * @param response
     * @return
     */
    public static String getFileName(HttpResponse response) {
        Header contentHeader = response.getFirstHeader("Content-Disposition");
        String filename = null;
        if (contentHeader != null) {
            HeaderElement[] values = contentHeader.getElements();
            if (values.length == 1) {
                NameValuePair param = values[0].getParameterByName("filename");
                if (param != null) {
                    try {
                        //filename = new String(param.getValue().toString().getBytes(), "utf-8");  
                        //filename=URLDecoder.decode(param.getValue(),"utf-8");  
                        filename = param.getValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return filename;
    }

    /**
     * 获取随机文件名
     *
     * @return
     */
    public static String getRandomFileName() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static void outHeaders(HttpResponse response) {
        Header[] headers = response.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            System.out.println(headers[i]);
        }
    }

    static class DownloadPath {
        String url;
        String path;

        public DownloadPath(String url, String path) {
            this.url = url;
            this.path = path;
        }
    }
}