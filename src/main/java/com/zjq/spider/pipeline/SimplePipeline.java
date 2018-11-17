package com.zjq.spider.pipeline;


import com.zjq.spider.model.Product;
import com.zjq.spider.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * User: zjq
 * Date: 18-9-20
 * Time: 下午2:14
 * Description:
 */
@Component
public class SimplePipeline implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ProductService productService;

    @Override
    public void process(ResultItems resultItems, Task task) {
        //gbm-china
        Product product = resultItems.get("product");
        //product不为空即详情页信息
        if (product != null) {
            this.save(product);
        }
        List<Product> productList = resultItems.get("productList");
        if (productList != null) {
            productList.stream().forEach(product1 -> this.save(product1));
        }
        //instrument

    }

    private void save(Product product) {
        Product oldProduct = productService.findByUrl(product.getGoodsUrl());
        if (oldProduct != null) {
            //如果创建时间为今天，则更新时间赋值null
            LocalDate localDate = LocalDate.now();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String createdTime = sdf.format(oldProduct.getCreatedTime());
            if (createdTime.contains(localDate.toString()))
                oldProduct.setUpdatedTime(null);
            else
                oldProduct.setUpdatedTime(new Date());
            oldProduct.setGoodsNumber(product.getGoodsNumber());
            oldProduct.setGoodsName(product.getGoodsName());
            oldProduct.setGoodsWeight(product.getGoodsWeight());
            oldProduct.setShopPrice(product.getShopPrice());
            oldProduct.setGoodsDesc(product.getGoodsDesc());
            oldProduct.setGoodsImg(product.getGoodsImg());
            oldProduct.setParams(product.getParams());
            oldProduct.setStandardValue(product.getStandardValue());
            oldProduct.setCdsNumber(product.getCdsNumber());
            oldProduct.setDevelopmentUnit(product.getDevelopmentUnit());
            oldProduct.setCat(product.getCat());
            productService.update(oldProduct);
        } else {
            productService.save(product);
        }
    }


}
