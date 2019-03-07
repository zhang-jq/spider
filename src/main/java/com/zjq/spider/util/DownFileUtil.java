package com.zjq.spider.util;

import com.zjq.spider.model.ProductFile;
import com.zjq.spider.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@Component
public class DownFileUtil {

    private Logger logger = LoggerFactory.getLogger(DownFileUtil.class);

    @Resource
    private ProductService productService;


    /**
     * 根据表名获取某个类型未下载过的文件进行下载
     *
     * @param tableName 表名
     * @param type      文件类型
     * @param filePath  文件下载路径
     */
    @Async
    public void down(String tableName, String type, String filePath) {
        ProductFile p = ProductFile.builder().goodsFileType(type).build();
        List<ProductFile> productFiles = productService.findProductFile(p, tableName);
        productFiles.stream().forEach(productFile -> {
            if (!StringUtils.isEmpty(productFile.getGoodsFileUrl())) {
                String url = productFile.getGoodsFileUrl();
                String fileName = filePath + url.substring(url.lastIndexOf("/"));
                boolean flag = this.downFileAsync(url, filePath, fileName);
                //更新文件状态
                productService.updateProductFile(ProductFile.builder()
                        .id(productFile.getId())
                        .status(flag ? "success" : "error").build(), tableName);
            }

        });
    }


    private boolean downFileAsync(String urlString, String filePath, String fileName) {

        boolean flag = false;//是否下载成功
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
        FileOutputStream out = null;
        InputStream ins = null;
        try {
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();
            out = new FileOutputStream(fileName);
            ins = con.getInputStream();
            byte[] b = new byte[1024];
            int i = 0;
            while ((i = ins.read(b)) != -1) {
                out.write(b, 0, i);
            }
            flag = true;
        } catch (Exception e) {
            System.out.println("下载文件时出现异常：" + e.getMessage());
            logger.error("下载文件时出现异常：", e);
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return flag;
    }
}
