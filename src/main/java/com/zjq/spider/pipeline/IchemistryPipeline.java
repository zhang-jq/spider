package com.zjq.spider.pipeline;

import com.zjq.spider.model.Product;
import com.zjq.spider.model.ProductFile;
import com.zjq.spider.model.ProductParam;
import com.zjq.spider.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;
import java.util.Date;

/**
 * User: zjq
 * Date: 19-3-7
 * Time: 上午11:48
 * Description: http://www.ichemistry.cn
 */
@Component
public class IchemistryPipeline implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ProductService productService;

    private static final String TABLE = "product_ichemistry";

    private static final String FILE_TABLE = "product_ichemistry_file";

    @Override
    public void process(ResultItems resultItems, Task task) {
        Product product = resultItems.get("product");
        if (product == null)
            return;
        Product oldProduct = productService.findByUrlByTableName(product.getGoodsUrl(), TABLE);
        if (oldProduct != null) {
            oldProduct.setUpdatedTime(new Date());
            oldProduct.setGoodsName(product.getGoodsName());
            oldProduct.setGoodsEnglishName(product.getGoodsEnglishName());
            oldProduct.setGoodsAlias(product.getGoodsAlias());
            oldProduct.setGoodsDesc(product.getGoodsDesc());
            oldProduct.setCasNumber(product.getCasNumber());
            oldProduct.setMolecularFormula(product.getMolecularFormula());
            oldProduct.setMolecularStructure(product.getMolecularStructure());
            oldProduct.setMolecularWeight(product.getMolecularWeight());
            productService.updateByTableName(oldProduct, TABLE);
        } else {
            int id = productService.saveByTableName(product, TABLE);
            ProductFile productFile = ProductFile.builder()
                    .goodsId(id)
                    .goodsUrl(product.getGoodsUrl())
                    .goodsFileUrl(product.getMolecularStructure())
                    .goodsFileType("pic").build();
            productService.addFileByTableName(productFile, FILE_TABLE);
        }
    }
}
