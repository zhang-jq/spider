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
import java.util.List;

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
        if (product != null) {
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
                productService.updateIchemistryProduct(oldProduct);
            } else {
                int id = productService.saveIchemistryProduct(product);
                ProductFile productFile = ProductFile.builder()
                        .goodsId(id)
                        .goodsUrl(product.getGoodsUrl())
                        .goodsFileUrl(product.getMolecularStructure())
                        .goodsFileType("pic").build();
                productService.addFileByTableName(productFile, FILE_TABLE);
            }
        }
        Integer num = resultItems.get("pdfNum");
        if (num != null) {
            ProductFile p = ProductFile.builder().goodsFileType("pdf").status("all").build();
            List<ProductFile> productFileList = productService.findProductFile(p, FILE_TABLE);
            if (num > productFileList.size())
                for (int i = productFileList.size() + 1; i < num; i++) {
                    String url = "http://www.ichemistry.cn/msds/pdf/vip" + i + ".pdf";
                    ProductFile productFile = ProductFile.builder().goodsFileType("pdf").goodsFileUrl(url)
                            .goodsUrl("http://www.ichemistry.cn/msds/" + i + ".htm").build();
                    productService.addFileByTableName(productFile, FILE_TABLE);

                }

        }

    }
}
