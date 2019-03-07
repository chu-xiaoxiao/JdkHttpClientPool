# JdkHttpClientPool
Java10 Httpclient 连接池

Get Post调用示例          
System.out.println(HttpRequest.newRequest().url("http://www.baidu.com")
        .header("Access-Control-Allow-Headers","Origin,No-Cache,X-Requested-With,If-Modified-Since,Pragma,Last-Modified,Cache-Control,Expires,Content-Type,Access-Control-Allow-Credentials",
                "User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.119 Safari/537.36")
        .Get(HttpResponse.BodyHandlers.ofByteArray()));
System.out.println(HttpRequest.newRequest().url("http://www.baidu.com")
        .GetString());
System.out.println(HttpRequest.newRequest().url("http://www.baidu.com")
        .header("Access-Control-Allow-Headers","Origin,No-Cache,X-Requested-With,If-Modified-Since,Pragma,Last-Modified,Cache-Control,Expires,Content-Type,Access-Control-Allow-Credentials",
                "User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.119 Safari/537.36")
        .body("body")
        .Post(HttpResponse.BodyHandlers.ofByteArray()));
System.out.println(HttpRequest.newRequest().url("http://www.baidu.com")
        .body("body")
        .PostString());

多线程下载
队列存放下载地址，多线程消费队列。
调用方法 直接调用静态下载方法

String filename = img.split("/")[img.split("/").length-1];
String path = String.format("/Users/zhangyuchen/Downloads/imgs/%s/%s/%s",searchWord,zhuanLan.getTitle(),filename);
HttpDownload.download(img,path);
