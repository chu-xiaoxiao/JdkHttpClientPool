package old;

import old.bean.MeiTuanShangHh;
import com.alibaba.fastjson.JSONObject;
import old.handle.meituan.HandleMeiTuan;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zhangyuchen
 * @date : 2019/1/9 17:25
 */
public class MeiTuan {



    public static void main(String[] args) {
        HandleMeiTuan handleMeiTuan = new HandleMeiTuan(null,null);
        List<MeiTuanShangHh> shangHhs = new ArrayList<>();
        for (int i = 1;i <=6 ; i++) {
            try {
                System.out.println(String.format("==============第%d页================", i));
                handleMeiTuan.getPoiId("c56b14", i).forEach(poiId -> {
                    try{
                        MeiTuanShangHh meiTuanShangHh = handleMeiTuan.getShangHu(poiId);
                        System.out.println(JSONObject.toJSON(meiTuanShangHh));
                        shangHhs.add(meiTuanShangHh);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });
            }catch (Exception e ){
                e.printStackTrace();
            }
        }
        handleMeiTuan.saveShangHuToExcel(shangHhs);
    }
}
