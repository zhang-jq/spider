package com.zjq.spider.web;

import com.zjq.spider.downloader.SimpleHttpClientDownloader;
import com.zjq.spider.pipeline.InstrumentPipeline;
import com.zjq.spider.pipeline.SimplePipeline;
import com.zjq.spider.processor.GbwChinaProcessor;
import com.zjq.spider.processor.InstrumentProcessor;
import com.zjq.spider.service.ProductService;
import com.zjq.spider.spider.SimpleSpider;
import com.zjq.spider.util.DownFileUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.monitor.SpiderMonitor;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
public class ProductController {

    @Resource
    private SimplePipeline simplePipeline;

    @Resource
    private InstrumentPipeline instrumentPipeline;

    @Resource
    private DownFileUtil downFileUtil;

    @GetMapping("/run")
    public String run() {
        try {
            SimpleHttpClientDownloader downloader = new SimpleHttpClientDownloader();
            SimpleSpider spider = SimpleSpider.create(new GbwChinaProcessor())
                    .addUrl("http://www.gbw-china.com")
                    .addPipeline(simplePipeline)
                    .setUUID(UUID.randomUUID().toString().replace("-", ""))
                    .thread(1);
            downloader.setUUID(spider.getUUID());
            spider.setDownloader(downloader);

            try {
                SpiderMonitor.instance().register(spider);
            } catch (Exception e) {
                e.printStackTrace();
            }
            spider.start();
            return "开始执行任务！";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "执行任务失败！";
    }

    @GetMapping("/run-instrument")
    public String runInstrument() {
        try {
            SimpleHttpClientDownloader downloader = new SimpleHttpClientDownloader();
            SimpleSpider spider = SimpleSpider.create(new InstrumentProcessor())
                    .addUrl("https://www.instrument.com.cn")
                    .addPipeline(instrumentPipeline)
                    .setUUID(UUID.randomUUID().toString().replace("-", ""))
                    .thread(1);
            downloader.setUUID(spider.getUUID());
            spider.setDownloader(downloader);

            try {
                SpiderMonitor.instance().register(spider);
            } catch (Exception e) {
                e.printStackTrace();
            }
            spider.start();
            return "开始执行任务！";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "执行任务失败！";
    }

    @GetMapping("/down-ichemistry-pic")
    public String down() {
        downFileUtil.down("product_ichemistry_file", "pic", "/home/zjq/downtest");
        return "开始执行图片下载任务！";
    }

}
