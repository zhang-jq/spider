package com.zjq.spider.web;

import com.zjq.spider.downloader.SimpleHttpClientDownloader;
import com.zjq.spider.model.ProductUrl;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Resource
    private ProductService productService;

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

    /**
     * 获取所有连接
     * @return
     */
    @GetMapping("/run-ichemistry-get-url")
    public String runIchemistryGetUrl(String page) {
//        String url = "http://www.ichemistry.cn/chemtool/chemicals.asp?type=getAllUrl";
        String url = StringUtils.isEmpty(page) ? "http://www.ichemistry.cn/chemtool/chemicals.asp?type=getAllUrl" : "http://www.ichemistry.cn/chemtool/chemicals.asp?type=getAllUrl&Page=" + page;
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

    /**
     * 根据上面方法抓取的链接抓取产品
     * @param page
     * @return
     */
    @GetMapping("/run-ichemistry-by-url")
    public String runIchemistryByUrl(String page) {
        //获取还未爬虫的链接
        List<ProductUrl> productUrls = productService.findNotSpiderProductUrl(ProductUrl.builder().type("ichemistry").build(), "product_ichemistry");
        List<String> urls = productUrls.stream().map(productUrl -> productUrl.getUrl()).collect(Collectors.toList());
        try {
            SimpleHttpClientDownloader downloader = new SimpleHttpClientDownloader();
            SimpleSpider spider = SimpleSpider.create(new IchemistryProcessor())
                    .addUrl(urls.toArray(new String[urls.size()]))
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

    @GetMapping("/run-ichemistry-msds")
    public String runIchemistryMsds(String page) {
        String url = StringUtils.isEmpty(page) ? "http://www.ichemistry.cn/msds/index.asp" : "http://www.ichemistry.cn/msds/index.asp?Page=" + page;
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

    @GetMapping("/run-ichemistry-danger")
    public String runIchemistryDanger(String page) {
        String url = StringUtils.isEmpty(page) ? "http://www.ichemistry.cn/weixianpin/index.asp" : "http://www.ichemistry.cn/weixianpin/index.asp?Page=" + page;
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
        String url = isVip ? "http://www.ichemistry.cn/msds/?flag=pdf" : "http://www.ichemistry.cn/msds/?type=novip&flag=pdf";
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
