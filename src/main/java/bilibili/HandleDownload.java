package bilibili;

import lombok.extern.slf4j.Slf4j;
import old.bean.ZhuanLan;

import java.io.IOException;

/**
 * @author : zhangyuchen
 * @date : 2018/12/2 12:36
 */
@Slf4j
public class HandleDownload {

    private HandleGet handleGet;

    public HandleGet getHandleGet() {
        return handleGet;
    }

    public void setHandleGet(HandleGet handleGet) {
        this.handleGet = handleGet;
    }

    /**
     * 根据查询结果下载所有页面图片
     * @param searchWord 查询关键字
     * @param page 下载查询的页数 -1下载全部
     */
    public void downLoadAllZhuanlanImgs(String searchWord,Integer page) throws IOException {
        handleGet.getZhuanlan(searchWord,page).forEach(zhuanLan -> downloadImgs(zhuanLan,searchWord));
    }

    /**
     * 根据查询页面URL获取所有图片
     * @param url
     */
    public void downLoadZhuanlanImgs(String url) throws IOException {
        downloadImgs(handleGet.getZhuanlan(url));
    }

    private void downloadImgs(ZhuanLan zhuanLan){
        downloadImgs(zhuanLan,"fromURL");
    }

    private void downloadImgs(ZhuanLan zhuanLan,String searchWord){
        log.info("当前解析专栏名称[{}]地址[{}]",zhuanLan.getTitle(),zhuanLan.getImgs());
        zhuanLan.getImgs().forEach(img -> {
            String filename = img.split("/")[img.split("/").length-1];
            String path = String.format("/Users/zhangyuchen/Downloads/imgs/%s/%s/%s",searchWord,zhuanLan.getTitle(),filename);
            HttpDownload.download(img,path);
        });
    }
}
