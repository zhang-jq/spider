package com.zjq.spider.processor;

import com.zjq.spider.Constant;
import com.zjq.spider.downloader.SimpleHttpClientDownloader;
import com.zjq.spider.model.Product;
import com.zjq.spider.pipeline.InstrumentPipeline;
import com.zjq.spider.pipeline.SimplePipeline;
import com.zjq.spider.spider.SimpleSpider;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.management.JMException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InstrumentProcessor implements PageProcessor {

    private static final String BASE = "https://www.instrument.com.cn";

    @Override
    public void process(Page page) {
        String url = page.getUrl().toString();
        Document doc = page.getHtml().getDocument();
        if (BASE.equals(url)) {
            List alist = Stream.of(
                    "https://www.instrument.com.cn/netshow/SH100277/product-C0-0-0-1.htm",
                    "https://www.instrument.com.cn/netshow/sh100320/product-C0-0-0-1.htm",
                    "https://www.instrument.com.cn/netshow/sh100320/qutinfo-P0-0-0-1.htm",
                    "https://www.instrument.com.cn/netshow/SH100650/product-C0-0-0-1.htm",
                    "https://www.instrument.com.cn/netshow/SH100650/qutinfo-P0-0-0-1.htm",
                    "https://www.instrument.com.cn/netshow/SH102464/product-C0-0-4877-1.htm",
                    "https://www.instrument.com.cn/netshow/SH102130/product-C0-0-4877-1.htm",
                    "https://www.instrument.com.cn/netshow/SH102043/product-C0-0-4877-1.htm",
                    "https://www.instrument.com.cn/netshow/SH100750/product-C0-0-4877-1.htm",
                    "https://www.instrument.com.cn/netshow/SH100750/qutInfo.htm",
                    "https://www.instrument.com.cn/netshow/SH100749/product-C0-0-4877-1.htm",
                    "https://www.instrument.com.cn/netshow/SH100749/qutInfo.htm",
                    "https://www.instrument.com.cn/netshow/sh100244/product-C0-0-4877-1.htm",
                    "https://www.instrument.com.cn/netshow/sh100244/qutInfo.htm",
                    "https://www.instrument.com.cn/netshow/SH100296/product-C0-0-4877-1.htm",
                    "https://www.instrument.com.cn/netshow/SH100328/product-C0-0-4877-1.htm"
            ).collect(Collectors.toList());
            page.addTargetRequests(alist);
        } else if (url.toUpperCase().contains("PRODUCT") || url.toUpperCase().contains("QUTINFO")) {
            List alist = doc.select("div[class=F18 Fw L30 L]")
                    .stream()
                    .map(a -> a.select("a").attr("abs:href"))
                    .collect(Collectors.toList());
            if (doc.select("a[class=next]").size() > 0) {
                alist.add(doc.select("a[class=next]").attr("abs:href"));
            }
            page.addTargetRequests(alist);
        } else {
            Product product = new Product();
            //连接
            product.setGoodsUrl(url);
            //名字
            Elements name = doc.select("div[class=L40 Fw F18 L]");
            if (name.size() > 0) {
                product.setGoodsName(name.text());
            }
            //分类
            Elements cat = doc.select("div[class= W960 L14 G6]");
            if (cat.size() > 0) {
                product.setCat(cat.text());
            }
            //图片
            Elements image = doc.select("#ProPicBig");
            if (image.size() > 0) {
                product.setGoodsImg(image.attr("abs:src"));
            }
            //参数
            Elements params = doc.select("div[class=Line F14 G6]");
            if (params.size() > 1) {
                //核心参数
                product.setParams(params.get(1).html());
            }
            if (params.size() > 0) {
                //获取型号和基本参数
                Elements trEles = params.get(0).select("tr");
                List<Map<String, String>> paramList = new ArrayList<>();
                trEles.stream().forEach(tr -> {
                    Elements tdEles = tr.select("td");
                    if (tdEles.size() % 2 == 0) {
                        for(int i = 0; i < tdEles.size(); i = i + 2) {
                            if (tdEles.get(i).text().contains("型号")) {
                                product.setGoodsSn(tdEles.get(i + 1).text());
                            } else {
                                Map paramMap = new HashMap();
                                String key = tdEles.get(i).text();
                                if (!StringUtils.isEmpty(key) && !key.contains("信息完整度")) {
                                    String value = "";
                                    if (key.contains("样本")) {
                                        if (tdEles.get(i + 1).select("a").size() > 0) {
                                            value = tdEles.get(i + 1).select("a").attr("abs:href");
                                        } else {
                                            value = tdEles.get(i + 1).text();
                                        }
                                    } else {
                                        value = tdEles.get(i + 1).text();
                                    }

                                    paramMap.put("name", key.trim().replace("：", ""));
                                    paramMap.put("value", value.trim());
                                    paramList.add(paramMap);
                                }

                            }
                        }
                    }
                });
                product.setParamList(paramList);
            }

            Elements desc = doc.select("div[class=BrWh Line]");
            if (desc.size() > 0) {
                product.setGoodsDesc(desc.html());
            }
            page.putField("product", product);
        }
    }

    @Override
    public Site getSite() {
        Site site = Constant.site()
                .setTimeOut(120 * 1000)
                .setSleepTime(0)
                .setCharset("UTF-8").setDisableCookieManagement(true);
        return site;
    }

    public static void main(String[] args) {
        SimpleHttpClientDownloader downloader = new SimpleHttpClientDownloader();

        SimpleSpider spider = SimpleSpider.create(new InstrumentProcessor())
                .addUrl("https://www.instrument.com.cn/netshow/SH100277/C261798.htm")
//                .addPipeline(simplePipeline)
                .addPipeline(new InstrumentPipeline())
                .setUUID("https://www.instrument.com.cn")  //spider 使用uuid确定唯一性
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
