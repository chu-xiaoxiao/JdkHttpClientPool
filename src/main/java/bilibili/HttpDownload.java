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
     *
     * 将下载下载任务添加到下载队列中。
     * @param url 下载地址
     * @param filepath 保存路径
     * @return
     */
    public static void download(String url, String filepath) {
        DOWNLOAD_PATHS.add(new DownloadPath(url, filepath));
    }

    /**
     * 下载线程
     * 下载线程从下载队列中取出下载任务并执行下载操作
     */
    static class DownloadThread implements Runnable {

        volatile Boolean done = true;

        @Override
        public void run() {
            for (; ; ) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    while (!this.done || CollectionUtils.isEmpty(DOWNLOAD_PATHS)) {
                        Thread.sleep(100);
                    }
                    DownloadPath downloadPath = DOWNLOAD_PATHS.remove();
                    this.done = false;
                    Path savePath = Path.of(downloadPath.path);
                    Files.createDirectories(savePath.getParent());
                    log.info("文件写入位置 {} 下载路径{}", savePath, downloadPath.url);
                    HttpRequest.newDefaultRequest().url(downloadPath.url)
                            .Get(java.net.http.HttpResponse.BodyHandlers.ofFile(savePath));
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