package com.zjq.spider.scheduling;

import com.zjq.spider.downloader.SimpleHttpClientDownloader;
import com.zjq.spider.pipeline.InstrumentPipeline;
import com.zjq.spider.pipeline.SimplePipeline;
import com.zjq.spider.processor.GbwChinaProcessor;
import com.zjq.spider.processor.InstrumentProcessor;
import com.zjq.spider.spider.SimpleSpider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 定时爬虫
 */
@Component
public class SpiderScheduler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private SimplePipeline simplePipeline;

    @Resource
    private InstrumentPipeline instrumentPipeline;

    @Scheduled(cron = "0 0 0 1 * ?")
//    @Scheduled(fixedRate = 1000 * 60 * 60)
    public void run() {

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

        } catch (Exception e) {
            e.printStackTrace();
        }

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
