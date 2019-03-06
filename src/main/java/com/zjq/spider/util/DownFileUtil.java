package com.zjq.spider.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

@Component
public class DownFileUtil {

    private Logger logger = LoggerFactory.getLogger(DownFileUtil.class);

//    @Async
    public void downFileAsync(String urlString, String fileName) {
        FileOutputStream out = null;
        InputStream ins = null;
        try{
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();
            out = new FileOutputStream(fileName);
            ins = con.getInputStream();
            byte[] b = new byte[1024];
            int i=0;
            while((i=ins.read(b))!=-1){
                out.write(b, 0, i);
            }
        } catch (Exception e){
            System.out.println("下载文件时出现异常：" + e.getMessage());
            logger.error("下载文件时出现异常：", e);
        } finally {
            if (ins != null) {
                try {ins.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
