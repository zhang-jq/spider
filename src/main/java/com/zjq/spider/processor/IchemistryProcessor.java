package com.zjq.spider.processor;

import com.zjq.spider.Constant;
import com.zjq.spider.downloader.SimpleHttpClientDownloader;
import com.zjq.spider.model.Product;
import com.zjq.spider.pipeline.IchemistryPipeline;
import com.zjq.spider.spider.SimpleSpider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
//        Document doc = null;
        Document doc = page.getHtml().getDocument();//使用这个会中文乱码
//        //解决乱码
//        try {
//            doc = Jsoup.parse(new URL(url).openStream(), "GBK", url);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        if (doc == null) {
            return;
        }
        if (url.contains("type=getAllUrl")) {//抓取所有产品链接
            List productUrls = new ArrayList();
            Elements trEles = doc.select("#container-right tbody tr");
            trEles.stream().forEach(tr -> {
                Elements aEles = tr.select("a");
                if (aEles.size() > 0) {
                    productUrls.add(aEles.get(0).attr("abs:href"));
                }
            });
            doc.select("p[align=center] a").stream().forEach(a -> {
                if (a.text().contains("下一页")) {
                    page.addTargetRequest(a.attr("abs:href") + "&type=getAllUrl");
                }
            });

            page.putField("productUrls", productUrls);

        } else if (url.contains("/chemtool/chemicals.asp")) {
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
            int flag = 0 ;//记录doc中移除tr的次数
            for (int i = 0; i < trEles.size(); i++) {
                Element tr = trEles.get(i);
                Elements tdEles = tr.select("td");
                if (tdEles.size() > 1) {
                    String name = tdEles.get(0).text();
                    String value = tdEles.get(1).text();
                    if (name.contains("中文名")) {
                        product.setGoodsName(value);
                        doc.select("table[class=ChemicalInfo] tr").get(i - flag++).remove();
                    } else if (name.contains("英文名")) {
                        product.setGoodsEnglishName(value);
                        doc.select("table[class=ChemicalInfo] tr").get(i - flag++).remove();
                    } else if (name.contains("别名")) {
                        product.setGoodsAlias(value);
                        doc.select("table[class=ChemicalInfo] tr").get(i - flag++).remove();
                    } else if (name.contains("分子结构")) {
                        String img = tdEles.select("img").attr("abs:src");
                        product.setMolecularStructure(img);
                        doc.select("table[class=ChemicalInfo] tr").get(i - flag++).remove();
                    } else if (name.contains("分子式")) {
                        product.setMolecularFormula(value);
                        doc.select("table[class=ChemicalInfo] tr").get(i - flag++).remove();
                    } else if (name.contains("分子量")) {
                        product.setMolecularWeight(value);
                        doc.select("table[class=ChemicalInfo] tr").get(i - flag++).remove();
                    } else if (name.contains("CAS登录号")) {
                        product.setCasNumber("\"" + value + "\"");
                        doc.select("table[class=ChemicalInfo] tr").get(i - flag++).remove();
                    } else if (name.contains("msds报告")) {//不要这个
                        doc.select("table[class=ChemicalInfo] tr").get(i - flag++).remove();
                    }
                } else {//如果td数量少于2，则是标题或者广告或者无用信息，标题只去除“相关化学品信息”和“CAS Number”，其他都去除
                    //包含trclass，说明是标题
                    if (!tr.html().contains("trclass") || tr.html().contains("相关化学品信息") || tr.html().contains("CAS Number")) {
                        doc.select("table[class=ChemicalInfo] tr").get(i - flag++).remove();
                    }
                }
            }
            String desc = doc.select("table[class=ChemicalInfo]").outerHtml();
            if (!StringUtils.isEmpty(desc)) {
                desc = desc.replaceAll("<a href[^>]*>", "").replaceAll("</a>", "").replaceAll("<img\\s[^>]+/?>", "");
                product.setGoodsDesc(desc);
            }
            page.putField("product", product);
        } else if (url.contains("www.ichemistry.cn/msds") && url.contains("flag=pdf")) {//获取msds的pdf
            //如果含Page则是尾页
            if (url.contains("Page")) {
                //获取最后一个产品数字，因为该产品名按数字自增生成，获取最后一个就能知道所有产品
                Elements trEles = doc.select("#BodyBox table").get(0).select("tbody tr");
                if (trEles.size() > 0) {
                    Elements aEles = trEles.get(trEles.size() - 1).select("a");
                    if (aEles.size() > 0) {
                        String msds = aEles.get(0).text();
                        Integer num = Integer.valueOf(msds.replace("MSDS#" , ""));
                        if (url.contains("novip")) {
                            page.putField("noVipPdfNum", num);
                        } else {
                            page.putField("pdfNum", num);
                        }
                    }
                }
            } else {
                //获取尾页
                Elements aEles = doc.select("p[align=center] a");
                aEles.stream().forEach(a -> {
                    if (a.text().contains("尾")) {
                        String lastUrl = a.attr("abs:href");
                        page.addTargetRequest(lastUrl + "&flag=pdf");
                    }
                });
            }
        } else if (url.contains("www.ichemistry.cn/msds") && !url.contains("htm")) {//获取msds信息
            List alist = new ArrayList();
            List<Product> products = new ArrayList();
            Elements trEles = doc.select("#BodyBox table").get(0).select("tbody tr");
            trEles.stream().forEach(tr -> {
                Elements aEles = tr.select("a");
                if (aEles.size() > 0) {
                    String detailUrl = aEles.get(0).attr("abs:href");
                    alist.add(detailUrl);

                    //产品信息
                    Product product = new Product();
                    Elements tdEles = tr.select("td");
                    product.setGoodsUrl(detailUrl);
                    product.setGoodsNumber(tdEles.get(0).text());
                    product.setGoodsName(tdEles.get(1).text());
                    product.setGoodsEnglishName(tdEles.get(2).text());
                    product.setCasNumber("\"" + tdEles.get(3).text() + "\"");
                    product.setUnNumber(tdEles.get(4).text());
                    product.setDangerousGoodsNumber(tdEles.get(5).text());
                    products.add(product);
                }
            });
            doc.select("p[align=center] a").stream().forEach(a -> {
                if (a.text().contains("下一页")) {
                    alist.add(a.attr("abs:href"));
                }
            });
            page.addTargetRequests(alist);
            page.putField("msdsProducts", products);
        } else if (url.contains("www.ichemistry.cn/msds") && url.contains("htm")) {//获取msds信息
            Product msdsProduct = new Product();
            msdsProduct.setGoodsUrl(url);
            int tableSize = doc.select("#BodyBox table").size();
            if (tableSize > 0) {
                doc.select("#BodyBox table").get(tableSize - 1).remove();
                doc.select("#BodyBox table").get(0).select("tbody tr").get(0).remove();
                doc.select("#BodyBox table").get(0).select("tbody tr").get(0).remove();
                String desc = doc.select("#BodyBox").outerHtml();
                desc = desc.replaceAll("<a href[^>]*>", "").replaceAll("</a>", "").replaceAll("<img\\s[^>]+/?>", "");
                msdsProduct.setGoodsDesc(desc);
            }

            page.putField("msdsProduct", msdsProduct);
        } else if (url.contains("www.ichemistry.cn/weixianpin")) {//获取危险品
            List<Product> products = new ArrayList();
            Elements trEles = doc.select("#BodyBox table").get(1).select("tbody tr");
            trEles.stream().forEach(tr -> {
                Elements aEles = tr.select("a");
                if (aEles.size() > 0) {
                    String detailUrl = aEles.get(0).attr("abs:href");
                    //产品信息
                    Product product = new Product();
                    Elements tdEles = tr.select("td");
                    product.setGoodsUrl(detailUrl);
                    product.setDangerousGoodsNumber(tdEles.get(0).text());
                    product.setCat(tdEles.get(1).text());
                    product.setGoodsDesc(tdEles.get(2).text());
                    product.setGoodsName(tdEles.get(3).text());
                    product.setGoodsEnglishName(tdEles.get(4).text());
                    product.setCasNumber("\"" + tdEles.get(5).text() + "\"");
                    products.add(product);
                }
            });
            doc.select("p[align=center]").get(0).select("a").stream().forEach(a -> {
                if (a.text().contains("下一页")) {
                    page.addTargetRequest(a.attr("abs:href"));
                }
            });

            page.putField("dangerProducts", products);
        }
    }

    @Override
    public Site getSite() {
        Site site = Constant.site()
                .setTimeOut(120 * 1000)
                .setSleepTime(1001)
                .setCharset("GBK").setDisableCookieManagement(true);
        return site;
    }

    public static void main(String[] args) {
        SimpleHttpClientDownloader downloader = new SimpleHttpClientDownloader();

        SimpleSpider spider = SimpleSpider.create(new IchemistryProcessor())
//                .addUrl("http://www.ichemistry.cn/chemtool/chemicals.asp")
                .addUrl("http://www.ichemistry.cn/chemistry/50-00-0.htm")
//                .addUrl("http://www.ichemistry.cn/msds/")
//                .addUrl("http://www.ichemistry.cn/msds/?type=novip&flag=pdf")
//                .addUrl("http://www.ichemistry.cn/msds/1.htm")
//                .addUrl("http://www.ichemistry.cn/weixianpin/index.asp?Page=1")
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
