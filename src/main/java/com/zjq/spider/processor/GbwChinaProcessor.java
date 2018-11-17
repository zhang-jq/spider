package com.zjq.spider.processor;

import com.alibaba.fastjson.JSON;
import com.zjq.spider.Constant;
import com.zjq.spider.downloader.SimpleHttpClientDownloader;
import com.zjq.spider.model.Product;
import com.zjq.spider.pipeline.SimplePipeline;
import com.zjq.spider.spider.SimpleSpider;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.management.JMException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class GbwChinaProcessor implements PageProcessor {

    private static final String BASE = "http://www.gbw-china.com";

    //分类
    public final static Map<String, String> catalog = new HashMap() {{
        put("11_0_0_0_0_0", "食品标准物质");
        put("12_0_0_0_0_0", "环境标准物质");
        put("10_0_0_0_0_0", "职业卫生标准物质");
        put("9_0_0_0_0_0", "仪器检定标准物质");
        put("85_0_0_0_0_0", "药典及对照品");
        put("6_0_0_0_0_0", "地质矿产标准物质");
        put("5_0_0_0_0_0", "其他标准物质");
    }};

    @Autowired
    private static SimplePipeline simplePipeline;

    @Override
    public void process(Page page) {
        String url = page.getUrl().toString();
        Document doc = page.getHtml().getDocument();

        if (url.equals(BASE)) {
//            List<String> urlList = doc.select(".main_nav .left_nav .left_list .sub_list_i a")
//                    .stream().map(a -> a.attr("abs:href")).collect(Collectors.toList());
            List<String> urlList = Stream.of(
                    "http://www.gbw-china.com/list/11_0_0_0_0_0.html",
                    "http://www.gbw-china.com/list/12_0_0_0_0_0.html",
                    "http://www.gbw-china.com/list/10_0_0_0_0_0.html",
                    "http://www.gbw-china.com/list/9_0_0_0_0_0.html",
                    "http://www.gbw-china.com/list/85_0_0_0_0_0.html",
                    "http://www.gbw-china.com/list/6_0_0_0_0_0.html",
                    "http://www.gbw-china.com/list/5_0_0_0_0_0.html"
            ).collect(Collectors.toList());

            page.addTargetRequests(urlList);
        } else if (url.contains("http://www.gbw-china.com/list") || url.contains("http://www.gbw-china.com/Product/index.html?lists=")) {
            //获取产品详情页连接
            List<String> urlList = doc.select("table[class=goodslist]>tbody>tr")
                    .stream()
                    .filter(tr -> tr.select("td").size() > 0)
                    .map(tr -> tr.select("td a").attr("abs:href"))
                    .collect(Collectors.toList());
            //下一页
            if (!StringUtils.isEmpty(doc.select("a[class=next]").attr("abs:href"))) {
                urlList.add(doc.select("a[class=next]").attr("abs:href"));
            }
            page.addTargetRequests(urlList);

            List<Product> productList = this.productInfo(doc);

            //获取分类
            catalog.forEach((k, v) -> {
                if (url.indexOf(k) > -1) {
                    productList.stream().forEach(product -> product.setCat(v));
                    return;
                }
            });

            page.putField("productList", productList);
        } else if (url.contains("http://www.gbw-china.com/info")) {
            Product product = new Product();
            product.setGoodsUrl(url);
            //产品图片
            product.setGoodsImg(doc.select("div[class=img_info] img").attr("abs:src"));
            //产品详情
            product.setGoodsDesc(doc.select("div[class=sub_detail detail1 active]").html());
            page.putField("product", product);
        }
    }

    /**
     * 列表页产品基本信息
     *
     * @param doc
     * @return
     */
    private List<Product> productInfo(Document doc) {
        List<Product> list = doc.select("table[class=goodslist]>tbody>tr").stream()
                .filter(tr -> tr.select("td").size() > 0)
                .map(tr -> {
                    Product product = new Product();
                    Elements tds = tr.select("td");
                    if (tds.size() > 10) {
                        //编号
                        product.setGoodsSn(tds.get(0).select(".sub_td").text());
                        //名称
                        product.setGoodsName(tds.get(1).select(".sub_td1").text());
                        //链接
                        product.setGoodsUrl(tds.get(1).select("a").attr("abs:href"));
                        //重量/规格
                        product.setGoodsWeight(tds.get(4).text());
                        //售价
                        product.setShopPrice(tds.get(5).text());
                        //库存
                        product.setGoodsNumber(tds.get(6).text());

                        //属性
                        List<Map<String, Object>> params = new ArrayList<>();

                        Map<String, Object> param = new HashMap<>();
                        param.put("name", "标准值");
                        param.put("value", tds.get(2).select(".sub_td1").text());
                        params.add(param);
                        product.setStandardValue(tds.get(2).select(".sub_td1").text());

                        param = new HashMap<>();
                        param.put("name", "CAS号");
                        param.put("value", tds.get(3).text());
                        params.add(param);
                        product.setCdsNumber(tds.get(3).text());

                        param = new HashMap<>();
                        param.put("name", "货期");
                        param.put("value", tds.get(7).text());
                        params.add(param);

                        param = new HashMap<>();
                        param.put("name", "研制单位");
                        param.put("value", tds.get(9).text());
                        params.add(param);
                        product.setDevelopmentUnit(tds.get(9).text());

                        product.setParams(JSON.toJSONString(params));
                    }

                    return product;
                }).filter(Objects::nonNull).collect(Collectors.toList());
        return list;
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

        SimpleSpider spider = SimpleSpider.create(new GbwChinaProcessor())
                .addUrl("http://www.gbw-china.com/Product/index.html?lists=11_0_0_0_0_0")
//                .addPipeline(simplePipeline)
                .addPipeline(new SimplePipeline())
                .setUUID(BASE)  //spider 使用uuid确定唯一性
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
