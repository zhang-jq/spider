package com.zjq.spider.processor;

import com.zjq.spider.Constant;
import com.zjq.spider.downloader.SimpleHttpClientDownloader;
import com.zjq.spider.model.Product;
import com.zjq.spider.pipeline.IchemistryPipeline;
import com.zjq.spider.spider.SimpleSpider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.management.JMException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IchemistryProcessor implements PageProcessor {

    private static final String BASE = "http://www.ichemistry.cn";

    @Override
    public void process(Page page) {
        String url = page.getUrl().toString();
        Document doc = null;
//        Document doc = page.getHtml().getDocument();//使用这个会中文乱码
        //解决乱码
        try {
            doc = Jsoup.parse(new URL(url).openStream(), "GBK", url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (doc == null) {
            return;
        }
        if (url.contains("/chemtool/chemicals.asp")) {
            List alist = new ArrayList();
            Elements trEles = doc.select("#container-right tbody tr");
            trEles.stream().forEach(tr -> {
                Elements aEles = tr.select("a");
                if (aEles.size() > 0) {
                    alist.add(aEles.get(0).attr("abs:href"));
                }
            });
            doc.select("p[align=center] a").stream().forEach(a -> {
                if (a.text().contains("下一页")) {
                    alist.add(a.attr("abs:href"));
                }
            });

            page.addTargetRequests(alist);
        } else if (url.contains("/chemistry") && url.contains(".htm")) {
            Elements trEles = doc.select("table[class=ChemicalInfo] tr");
            if (trEles.size() == 0)
                return;
            Product product = new Product();
            product.setGoodsUrl(url);
            String desc = doc.select("table[class=ChemicalInfo]").outerHtml();
            if (!StringUtils.isEmpty(desc))
                product.setGoodsDesc(desc);
            trEles.stream().forEach(tr -> {
                Elements tdEles = tr.select("td");
                if (tdEles.size() > 1) {
                    String name = tdEles.get(0).text();
                    String value = tdEles.get(1).text();
                    if (name.contains("中文名")) {
                        product.setGoodsName(value);
                    } else if (name.contains("英文名")) {
                        product.setGoodsEnglishName(value);
                    } else if (name.contains("别名")) {
                        product.setGoodsAlias(value);
                    } else if (name.contains("分子结构")) {
                        String img = tdEles.select("img").attr("abs:src");
                        product.setMolecularStructure(img);
                    } else if (name.contains("分子式")) {
                        product.setMolecularFormula(value);
                    } else if (name.contains("分子量")) {
                        product.setMolecularWeight(value);
                    } else if (name.contains("CAS登录号")) {
                        product.setCasNumber(value);
                    }
                }
            });
            page.putField("product", product);
        } else {

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
