package com.zjq.spider.pipeline;


import com.zjq.spider.model.Product;
import com.zjq.spider.model.ProductParam;
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
 * Date: 18-11-17
 * Time: 下午16:43
 * Description: https://www.instrument.com.cn
 */
@Component
public class InstrumentPipeline implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ProductService productService;

    private static final String TABLE = "product_instrument";

    private static final String PARAM_TABLE = "product_instrument_param";

    @Override
    public void process(ResultItems resultItems, Task task) {
        Product product = resultItems.get("product");
        if (product == null)
            return;
        Product oldProduct = productService.findByUrlByTableName(product.getGoodsUrl(), TABLE);
        if (oldProduct != null) {
            //如果创建时间为今天，则更新时间赋值null
            LocalDate localDate = LocalDate.now();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String createdTime = sdf.format(oldProduct.getCreatedTime());
//            if (createdTime.contains(localDate.toString()))
//                oldProduct.setUpdatedTime(null);
//            else
                oldProduct.setUpdatedTime(new Date());
            oldProduct.setGoodsName(product.getGoodsName());
            oldProduct.setGoodsDesc(product.getGoodsDesc());
            oldProduct.setGoodsImg(product.getGoodsImg());
            oldProduct.setParams(product.getParams());
            oldProduct.setCat(product.getCat());
            productService.updateByTableName(oldProduct, TABLE);
            Integer id = oldProduct.getGoodsId();
            productService.deleteParamByTableName(id, PARAM_TABLE);
            if (product.getParamList() != null) {
                product.getParamList().stream().forEach(param -> {
                    ProductParam productParam = ProductParam.builder().productId(id).name(param.get("name")).value(param.get("value")).build();
                    productService.addParamByTableName(productParam, PARAM_TABLE);
                });
            }
        } else {
            Integer id = productService.saveByTableName(product, TABLE);
            if (product.getParamList() != null) {
                product.getParamList().stream().forEach(param -> {
                    ProductParam productParam = ProductParam.builder().productId(id).name(param.get("name")).value(param.get("value")).build();
                    productService.addParamByTableName(productParam, PARAM_TABLE);
                });
            }
        }
    }



}
