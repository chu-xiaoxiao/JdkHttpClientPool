package old;

import bilibili.HandleDownload;
import bilibili.HandleGet;

import java.io.IOException;

/**
 * @author : zhangyuchen
 * @date : 2018/11/20 16:01
 */
public class Bilibili {

    public static final HandleDownload HANDLE_DOWNLOAD = new HandleDownload();


    public static void main(String[] args) throws IOException {
        HANDLE_DOWNLOAD.setHandleGet(new HandleGet());
        ///两种搜索方法
        HANDLE_DOWNLOAD.downLoadZhuanlanImgs("https://www.bilibili.com/read/cv1698785");
        //HANDLE_DOWNLOAD.downLoadAllZhuanlanImgs("小柒的动漫壁纸精选",-1);


    }

}
