package old.handle.jd;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import old.handle.jd.bean.JD;
import old.handle.jd.bean.JDType;
import old.util.HttpClientUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author : zhangyuchen
 * @date : 2019/1/31 18:27
 */
@Slf4j
public class HandleJD {

    private static HttpClientUtils httpClientUtils = new HttpClientUtils();
    

    static List<JD> getList(Document root) throws IOException {
        var item = root.getElementsByClass("gl-item");
        var list = new ArrayList<JD>();
        for (Element element : item) {
            JD j = new JD();
            j.setDataSku(element.attr("data-sku"));
            j.setDataSpu(element.attr("data-spu"));
            j.setDataPid(element.attr("data-pid"));
            Elements a = element.getElementsByClass("p-name p-name-type-2");
            j.setName(a.get(0).getElementsByTag("em").first().text());
            j.setUrl("https:"+a.get(0).getElementsByTag("a").first().attr("href"));
            Elements p = element.getElementsByClass("p-price");
            j.setPrice(p.get(0).getAllElements().get(0).text());
            list.add(j);
            Elements ad  = element.getElementsByClass("curr-shop");
            if(ad.size()>0) {
                j.setAddr(ad.get(0).attr("title"));
            }
            log.info("商品{}",JSON.toJSONString(j));
        }
        return list;
    }


    static List<JD> getListType2(Document root) throws IOException {
        Elements item = root.getElementsByClass("gl-item");
        List<JD> list = new ArrayList<>();
        for (Element element : item) {
            JD j = new JD();
            Element skuItem = element.getElementsByClass("gl-i-wrap j-sku-item").first();
            j.setDataSku(skuItem.attr("data-sku"));
            j.setDataSpu(skuItem.attr("data-spu"));
            j.setDataPid(skuItem.attr("data-pid"));
            Elements a = skuItem.getElementsByClass("p-name");
            j.setName(a.get(0).getElementsByTag("em").first().text());
            j.setUrl("https:"+a.get(0).getElementsByTag("a").first().attr("href"));
            Elements p = skuItem.getElementsByClass("p-price");
            j.setPrice(p.get(0).getAllElements().get(0).text());
            list.add(j);
            Elements ad  = skuItem.getElementsByClass("curr-shop");
            if(ad.size()>0) {
                j.setAddr(ad.get(0).attr("title"));
            }
            log.info("商品{}",JSON.toJSONString(j));
        }
        return list;
    }

    static List<JDType> getTypes(Boolean hashSearch) throws IOException {
        List<JDType> typeURls = new ArrayList<>();
        try(Stream<String> lines = Files.lines(Paths.get("/Users/zhangyuchen/intellij/bilibili/src/main/resources/type.html"))){
            StringBuffer stringBuffer = new StringBuffer();
            lines.forEach(stringBuffer::append);
            Document document = Jsoup.parse(stringBuffer.toString());
            Elements items = document.getElementsByClass("item");
            for (Element item : items) {
                Elements chaoshiNavSubItems = item.getElementsByClass("chaoshi_nav_sub_item");
                for (Element chaoshiNavSubItem : chaoshiNavSubItems) {
                    for (Element chaoshiNavSubMainLink : chaoshiNavSubItem.getElementsByClass("chaoshiNavSubMainLink")) {
                        JDType jdType = new JDType();
                        jdType.setFirstType(chaoshiNavSubItem.getElementsByClass("chaoshi_nav_sub_title").first().html());
                        jdType.setSecondType(chaoshiNavSubMainLink.attr("title"));
                        if(hashSearch) {
                            jdType.setUrl("https://search.jd.com" + chaoshiNavSubMainLink.attr("href"));
                        }else{
                            jdType.setUrl("https:" + chaoshiNavSubMainLink.attr("href"));
                        }
                        typeURls.add(jdType);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return typeURls;
    }

    static Integer getPage(Document root){
        Element page =  root.getElementById("J_topPage");
        if(page.getElementsByTag("i").first()==null){
            log.warn("页面不存在");
            return 0;
        }
        Integer pageCount = Integer.parseInt(page.getElementsByTag("i").first().html());
        return pageCount*2-1;
    }

    static String getTypeStr(String url) throws UnsupportedEncodingException {
        url = URLDecoder.decode(url,"UTF-8");
        String type = url.split("Search\\?keyword=")[1].split("&")[0] ;
        return type;
    }


    public static void main(String[] args) throws IOException {
        var hashSearch = true;
        try (OutputStream out = new FileOutputStream(String.format("/Users/zhangyuchen/Downloads/京东超市.xlsx"))) {
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            Integer sheetNo  = 1;
            Integer maxType = getTypes(hashSearch).size();
            Sheet sheet1 = new Sheet(1, 0, JD.class);
            try{
                for (JDType jdType : getTypes(hashSearch)) {
                    log.info("总进度 {}/{} 一级类别[{}] 二级类别 [{}]",sheetNo++,maxType,jdType.getFirstType(),jdType.getSecondType());
                    Document document = httpClientUtils.getDocumentWinTime(jdType.getUrl(),1000L);
                    Integer maxPage = getPage(document);
                    for(int i=1;i<=maxPage;i+=2){
                        log.info("当前页面进度 {}/{} 一级类别[{}] 二级类别 [{}]",i,maxPage,jdType.getFirstType(),jdType.getSecondType());
                        List<JD> goodsItem = new ArrayList<>();
                        if(hashSearch) {
                            goodsItem = getList(httpClientUtils.getDocumentWinTime(String.format("%s&page=%d", jdType.getUrl(), i), 500L));
                        }else{
                            goodsItem = getListType2(httpClientUtils.getDocumentWinTime(String.format("%s&page=%d", jdType.getUrl(), i), 500L));
                        }
                        for (JD jd : goodsItem) {
                            jd.setFirstType(jdType.getFirstType());
                            jd.setSecondType(jdType.getSecondType());
                        }
                        writer.write(goodsItem, sheet1);
                    }
                }

            }catch (Exception e ){
                e.printStackTrace();
            }
            writer.finish();
            System.out.println("写入成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
