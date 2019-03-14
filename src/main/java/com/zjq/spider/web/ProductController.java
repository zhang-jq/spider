package com.zjq.spider.web;

import com.zjq.spider.downloader.SimpleHttpClientDownloader;
import com.zjq.spider.pipeline.IchemistryPipeline;
import com.zjq.spider.pipeline.InstrumentPipeline;
import com.zjq.spider.pipeline.SimplePipeline;
import com.zjq.spider.processor.GbwChinaProcessor;
import com.zjq.spider.processor.IchemistryProcessor;
import com.zjq.spider.processor.InstrumentProcessor;
import com.zjq.spider.service.ProductService;
import com.zjq.spider.spider.SimpleSpider;
import com.zjq.spider.util.DownFileUtil;
import org.springframework.util.StringUtils;
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

    @Resource
    private IchemistryPipeline ichemistryPipeline;

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

    @GetMapping("/run-ichemistry")
    public String runIchemistry(String page) {
        String url = StringUtils.isEmpty(page) ? "http://www.ichemistry.cn/chemtool/chemicals.asp" : "http://www.ichemistry.cn/chemtool/chemicals.asp?Page=" + page;
        try {
            SimpleHttpClientDownloader downloader = new SimpleHttpClientDownloader();
            SimpleSpider spider = SimpleSpider.create(new IchemistryProcessor())
                    .addUrl(url)
                    .addPipeline(ichemistryPipeline)
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

    @GetMapping("/run-ichemistry-pdf")
    public String runIchemistryPdf(boolean isVip) {
        //判断是否下载vip版的pdf
        String url = isVip ? "http://www.ichemistry.cn/msds/" : "http://www.ichemistry.cn/msds/?type=novip";
        try {
            SimpleHttpClientDownloader downloader = new SimpleHttpClientDownloader();
            SimpleSpider spider = SimpleSpider.create(new IchemistryProcessor())
                    .addUrl(url)
                    .addPipeline(ichemistryPipeline)
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
    public String downIchemistryPic() {
        downFileUtil.down("product_ichemistry_file", "pic", "D:\\SpiderFile\\Ichemistry\\pic");
        return "开始执行图片下载任务！";
    }

    @GetMapping("/down-ichemistry-pdf")
    public String downIchemistryPdf(boolean isVip) {
        String type = isVip ? "pdf" : "noVipPdf";
        downFileUtil.down("product_ichemistry_file", type, "D:\\SpiderFile\\Ichemistry\\pdf");
        return "开始执行PDF下载任务！";
    }

}
