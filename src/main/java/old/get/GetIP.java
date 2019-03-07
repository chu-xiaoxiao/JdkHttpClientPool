package old.get;

import old.bean.GNIP;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : zhangyuchen
 * @date : 2018/12/17 16:35
 */
public class GetIP {

    public static List<GNIP> getIP(Document document){
        Elements trs = document.getElementsByTag("tr");
        List<GNIP> ips = trs.stream().map(tr -> {
            Elements tds = tr.getElementsByTag("td");
            if(tds.size()>0) {
                return GNIP.builder().ip(tds.get(1).text()).port(tds.get(2).text()).build();
            }else{
                return null;
            }
        }).collect(Collectors.toList());
        return ips;
    }
}
