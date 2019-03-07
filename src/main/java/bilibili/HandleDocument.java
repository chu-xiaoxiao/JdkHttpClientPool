package bilibili;

import lombok.extern.slf4j.Slf4j;
import old.bean.ZhuanLan;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : zhangyuchen
 * @date : 2018/12/13 16:24
 */
@Slf4j
public class HandleDocument {
    /**
     * 获取当前搜索结果页所有页码
     * @param document
     * @return
     */
    public Integer getTotalPages(Document document){

        //获取当前页面显示的JS代码
        List<String> jsCodes = document.getElementsByTag("script").stream()
                .filter(element -> StringUtils.isNotBlank(element.html()))
                .filter(element -> element.html().contains("__INITIAL_STATE__"))
                .map(Element::html)
                .collect(Collectors.toList());
        //过滤获取的JS代码 取出中间的json
        String jsCode = jsCodes.get(0);
        String json = "{"+jsCode.split("=\\{")[1].split("};")[0]+"}";
        //JS代码转换JSON
        JSONObject jsCodeJson = JSONObject.parseObject(json);
        //获取最大页码
        Integer totalPages = jsCodeJson.getJSONObject("pageInfo").getInteger("totalPages");
        return totalPages;
    }


    /**
     * 获取专栏页面图片url
     * @param document
     * @return
     */
    public List<String> getImgUrls(Document document){
        Elements elements = document.getElementsByTag("img");
        List<String> imgs = new ArrayList<String>();
        elements.stream().filter(element -> StringUtils.isNotBlank(element.attr("data-src")))
                .forEach(element -> {
                    String imgUrl = element.attr("data-src");
                    imgUrl = imgUrl.contains("http://")||imgUrl.contains("https://")?imgUrl:"http:"+imgUrl;
                    imgs.add(imgUrl);
                });
        return imgs;
    }

    public String getTitle(Document document){
        Element element = document.getElementsByAttributeValue("name","title").first();
        if(element!=null){
            return element.attr("content");
        }
        return null;
    }

    /**
     *  根据查询页面获取所有查询到的专栏地址 以及专栏名
     */
    public List<ZhuanLan> getZhuanLans(Document document){
        Elements elements = document.getElementsByTag("a");
        List<ZhuanLan> result = elements.stream()
                .filter(element -> element.hasClass("cover"))
                .map(element -> ZhuanLan.builder()
                        .title(element.attr("title"))
                        .url("https:"+element.attr("href"))
                        .build())
                .collect(Collectors.toList());
        return result;
    }

}
