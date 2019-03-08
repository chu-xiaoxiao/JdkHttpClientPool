package bilibili;

import http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import thread.DownloadThreadFactory;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * 文件下载类
 * @author zhangyuchen
 * @date 2019年03月08日13:54:01
 */
@Slf4j
public class HttpDownload {


    private static final Queue<DownloadPath> DOWNLOAD_PATHS = new LinkedBlockingDeque<>();

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(6, 10, 10, TimeUnit.MINUTES, new LinkedBlockingDeque<>(),new DownloadThreadFactory("下载线程"));

    static {
        //初始化下载线程
        Stream.iterate(0, item -> ++item).limit(5).forEach(item -> threadPoolExecutor.execute(new DownloadThread()));
    }


    /**
     * 根据url下载文件，保存到filepath中
     *
     * @param url
     * @param filepath
     * @return
     */
    public static void download(String url, String filepath) {
        DOWNLOAD_PATHS.add(new DownloadPath(url, filepath));
    }

    static class DownloadThread implements Runnable {

        volatile Boolean done = true;

        @Override
        public void run() {
            for (; ; ) {
                try {
                    while (!this.done || CollectionUtils.isEmpty(DOWNLOAD_PATHS)) {
                        Thread.sleep(100);
                    }
                    DownloadPath downloadPath = DOWNLOAD_PATHS.remove();
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

    static class DownloadPath {
        String url;
        String path;

        public DownloadPath(String url, String path) {
            this.url = url;
            this.path = path;
        }
    }
}