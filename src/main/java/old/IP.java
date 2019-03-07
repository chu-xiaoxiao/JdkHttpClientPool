package old;

import old.bean.GNIP;
import com.alibaba.fastjson.JSON;
import old.get.GetIP;
import old.util.HttpClientUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author : zhangyuchen
 * @date : 2018/12/17 16:34
 */
public class IP {
    private static HttpClientUtils httpClientUtils = new HttpClientUtils();

    public static void main(String[] args) throws IOException {
        List<GNIP> result = GetIP.getIP(httpClientUtils.getDocument("https://www.xicidaili.com/nn/"));
        result.forEach(item -> System.out.println(JSON.toJSONString(item)));
    }

}
