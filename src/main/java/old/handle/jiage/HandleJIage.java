package old.handle.jiage;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import old.handle.jiage.bean.JiaGe;
import old.handle.meituan.HandleMeiTuan;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import old.util.HttpClientUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author : zhangyuchen
 * @date : 2019/1/24 11:36
 */
@Slf4j
public class HandleJIage {
    private static HttpClientUtils httpClientUtils = new HttpClientUtils();



    public HandleJIage(String ip,Integer port){
        if(StringUtils.isNotBlank(ip)&&port!=null) {
            httpClientUtils.setProxy(new HttpHost(ip, port));
        }
    }

    /**
     * 获取页面
     * @param url
     * @return
     */
    public Document getPage(String url) throws IOException {
        return httpClientUtils.getDocument(url);
    }

    /**
     * 获取所有分类请求路径
     * @param document
     * @return
     */
    List<String> getURL(Document document){
        Element element = document.getElementsByClass("w890 fl").get(0);
        return element.getElementsByTag("script").stream()
                .map(item -> item.html().trim().split("\\|"))
                .flatMap(Arrays::stream)
                .filter(item -> item.contains("/channel"))
                .map(item -> item.split("<a href=")[1].split(">")[0])
                .distinct()
                .map(item -> "http://nc.mofcom.gov.cn/"+item+"&par_p_index=11&startTime=2019-01-18&endTime=2019-01-25")
                .collect(Collectors.toList());
    }

    /**
     * 获取最大页码
     * @param document
     * @return
     */
    Integer getMaxPage(Document document){
        Elements elements = document.getElementsByTag("script");
        String pageCount =  elements.stream()
                .filter(item -> item.html().contains("v_PageCount"))
                .map(item -> item.html().split("v_PageCount = ")[1].split(";")[0])
                .findFirst().orElse("");
        if (StringUtils.isBlank(pageCount)){
            pageCount = "0";
        }
        return Integer.valueOf(pageCount);
    }


    public List<JiaGe> getJiaGe(Document document){
        Element element = document.getElementsByClass("table-01 mt30").stream().findFirst().get();
        List<JiaGe> jiaGes = element.getElementsByTag("tr").stream()
                .skip(1)
                .map(item -> {
                    Elements tds = item.getElementsByTag("td");
                    return JiaGe.builder()
                            .date(tds.get(0).html())
                            .name(tds.get(1).text())
                            .price(tds.get(2).text())
                            .addr(tds.get(3).text())
                            .build();
                })
                .collect(Collectors.toList());
        return jiaGes;
    }

    public static void main(String[] args) {
        HandleJIage handleJIage = new HandleJIage(null,null);
        Random random = new Random(System.currentTimeMillis());
        try (OutputStream out = new FileOutputStream(String.format("/Users/zhangyuchen/Downloads/北京价格.xlsx"))) {
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            Sheet sheet1 = new Sheet(1, 0, JiaGe.class);
            sheet1.setSheetName("价格");
            Document document = httpClientUtils.getDocument("http://nc.mofcom.gov.cn/channel/jghq2017/price_list.shtml");
            //获取所有页码
            List<String> urls =  handleJIage.getURL(document);
            Integer count = 0;
            for (String url : urls) {
                Integer maxPage =  handleJIage.getMaxPage(httpClientUtils.getDocumentWinTime(url,1100L));
                for (Integer i = 1; i <= maxPage; i++) {
                    System.out.println( url + "&page="+i);
                    List<JiaGe> jiages =  handleJIage.getJiaGe(httpClientUtils.getDocumentWinTime(url + "&page="+i,1100L)).stream().collect(Collectors.toList());
                    jiages.forEach(System.out::println);
                    writer.write(jiages, sheet1);
                }
                log.info("进度 [{}] 总数 [{}]",++count,urls.size());
            }
            writer.finish();
            System.out.println("写入成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
