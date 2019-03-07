package com.zjq.spider;

import com.zjq.spider.downloader.SimpleHttpClientDownloader;
import com.zjq.spider.pipeline.IchemistryPipeline;
import com.zjq.spider.processor.IchemistryProcessor;
import com.zjq.spider.spider.SimpleSpider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.monitor.SpiderMonitor;

import javax.management.JMException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpiderApplication.class)
public class SpiderApplicationTests {

    @Test
    public void contextLoads() {
        SimpleHttpClientDownloader downloader = new SimpleHttpClientDownloader();

        SimpleSpider spider = SimpleSpider.create(new IchemistryProcessor())
//                .addUrl("http://www.ichemistry.cn/chemtool/chemicals.asp")
                .addUrl("http://www.ichemistry.cn/chemistry/50-00-0.htm")
//                .addPipeline(simplePipeline)
                .addPipeline(new IchemistryPipeline())
                .setUUID("http://www.ichemistry.cn")  //spider 使用uuid确定唯一性
                .thread(1);
        downloader.setUUID(spider.getUUID());
        spider.setDownloader(downloader);

        try {
            SpiderMonitor.instance().register(spider);
        } catch (JMException e) {
            e.printStackTrace();
        }

        spider.start();
    }

}
