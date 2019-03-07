package bilibili;


import http.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import old.bean.ZhuanLan;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhangyuchen
 * @date : 2018/11/28 10:43
 */
@Slf4j
public class HandleGet {

    private HandleDocument handleDocument = new HandleDocument();

    /**
     * 根据查询关键字获取查询页面
     * @param searchWord
     * @return
     */
    public List<Document> getSearchPages(String searchWord) throws IOException {
       return getSearchPages(searchWord,-1);
    }

    public List<Document> getSearchPages(String searchWord,Integer page) throws IOException {
        log.info("获取所有查询结果页面 start");
        String requestUrl = String.format("https://search.bilibili.com/article?keyword=%s", URLEncoder.encode(searchWord, Charset.forName("UTF-8")));
        log.info("查询结果首页 {}",requestUrl);
        //获取查询结果页document
        Document document = HttpUtil.getDocument(requestUrl);
        Integer totalPages = 1;
        if(page>0){
            totalPages = page;
        }else{
            totalPages = handleDocument.getTotalPages(document);
        }
        List<Document> result = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            String url = requestUrl+"&page="+i;
            result.add(HttpUtil.getDocument(url));
            log.info("查询结果翻页{}",url);
        }
        log.info("获取所有查询结果页 end");
        return result;
    }


    /**
     * 获取专栏页面对应的所有图片
     * @param searchWord
     * @return
     */
    public List<ZhuanLan> getZhuanlan(String searchWord, Integer page) throws IOException {
        List<ZhuanLan> zhuanLans = new ArrayList<>();
        //获取当前查询条件所有的查询结果页面
        List<Document> searchPages = getSearchPages(searchWord,page);
        //获取当前页面所有专栏地址
        searchPages.forEach(searchPage -> zhuanLans.addAll(handleDocument.getZhuanLans(searchPage)));
        //根据专栏获取URL
        zhuanLans.forEach(zhuanLan -> zhuanLan.setImgs(handleDocument.getImgUrls(HttpUtil.getDocument(zhuanLan.getUrl()))));
        return zhuanLans;
    }

    public ZhuanLan getZhuanlan(String zhuanlanUrl) throws IOException {
        //获取专栏页面的Document文档
        Document pageDocument = HttpUtil.getDocument(zhuanlanUrl);

        //获取专栏页面所有图片链接
        List<String> imgUrls = handleDocument.getImgUrls(pageDocument);
        //获取专栏标题
        String title = handleDocument.getTitle(pageDocument);
        ZhuanLan zhuanLan = ZhuanLan.builder()
                .imgs(imgUrls)
                .title(title)
                .build();
        return zhuanLan;
    }

}
