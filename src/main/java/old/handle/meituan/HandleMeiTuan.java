package old.handle.meituan;

import lombok.extern.slf4j.Slf4j;
import old.bean.MeiTuanShangHh;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.jsoup.nodes.Document;
import old.util.HttpClientUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhangyuchen
 * @date : 2019/1/9 17:27
 */
@Slf4j
public class HandleMeiTuan {

    private static HttpClientUtils httpClientUtils = new HttpClientUtils();

    public HandleMeiTuan(String ip,Integer port){
        if(StringUtils.isNotBlank(ip)&&port!=null) {
            httpClientUtils.setProxy(new HttpHost(ip, port));
        }
    }

    /**
     * 获取商户号Id
     * @return
     */
    public List<String> getPoiId(String searchKey,Integer page) throws IOException {
        List<String> poiIds = new ArrayList<>();
        Document document = httpClientUtils.getDocument(String.format("https://bj.meituan.com/meishi/%s/pn%d/",searchKey,page));
        document.getElementsByTag("script").stream().filter(temp -> temp.html().contains("window._appState"))
                .findFirst()
        .ifPresent(
                element ->{
                    String temp = "{"+element.html().split("= \\{")[1];
                    temp = temp.split("\"comHeader\"")[0];
                    temp = temp.substring(0,temp.length()-1)+"}";
                    JSONObject root = JSON.parseObject(temp);
                    JSONObject poiLists = root.getJSONObject("poiLists");
                    JSONArray poiInfos = poiLists.getJSONArray("poiInfos");
                    for (int i = 0; i < poiInfos.size(); i++) {
                        String poiId =  poiInfos.getJSONObject(i).getString("poiId");
                        log.debug(poiId);
                        poiIds.add(poiId);
                    }
                }
        );
        return poiIds;
    }

    public MeiTuanShangHh getShangHu(String poiId) throws IOException {
        MeiTuanShangHh shangHh = new MeiTuanShangHh();
        Document document = httpClientUtils.getDocument(String.format("https://www.meituan.com/meishi/%s/", poiId));
        document.getElementsByTag("script").stream().filter(element -> element.html().contains("window._appState")).findFirst()
                .ifPresent(page -> {
                    String temp = page.toString().split("= \\{")[1];
                    temp = "{"+temp;
                    temp = temp.split("\"comHeader\"")[0];
                    temp = temp.substring(0,temp.length()-1)+"}";
                    JSONObject root = JSONObject.parseObject(temp);
                    JSONObject detailInfo = root.getJSONObject("detailInfo");
                    shangHh.setPhone(detailInfo.getString("phone"));
                    shangHh.setTitle(detailInfo.getString("name"));
                    shangHh.setOpenTime(detailInfo.getString("openTime"));
                    shangHh.setLongitude(detailInfo.getString("longitude"));
                    shangHh.setLatitude(detailInfo.getString("latitude"));
                    shangHh.setAddress(detailInfo.getString("address"));
                    JSONArray crumbNav = root.getJSONArray("crumbNav");
                    shangHh.setType(crumbNav.getJSONObject(crumbNav.size()-1).getString("title"));
                });
        return shangHh;
    }

    public void saveShangHuToExcel(List<MeiTuanShangHh> meiTuanShangHhs) {
        this.saveShangHuToExcel(meiTuanShangHhs,null,0);
    }

    public void saveShangHuToExcel(List<MeiTuanShangHh> meiTuanShangHhs,String sheetName,Integer sheetId) {
        String shopType = meiTuanShangHhs.get(0).getType();
        try (OutputStream out = new FileOutputStream(String.format("/Users/zhangyuchen/Downloads/%s.xlsx",shopType))) {
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            Sheet sheet1 = new Sheet(1, 0,MeiTuanShangHh.class);
            Sheet sheet2 = new Sheet(3, 2,MeiTuanShangHh.class);

            sheet1.setSheetName(sheetName);
            writer.write(meiTuanShangHhs, sheet1);
            writer.write(meiTuanShangHhs,sheet2);
            writer.finish();
            System.out.println("写入成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
