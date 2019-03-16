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

    private static final String TABLE_MSDS = "product_ichemistry_msds";

    private static final String TABLE_DANGER = "product_ichemistry_danger";

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

        List<Product> msdsProducts = resultItems.get("msdsProducts");
        if (msdsProducts != null) {
            msdsProducts.forEach(product1 -> {
                Product oldProduct = productService.findByUrlByTableName(product1.getGoodsUrl(), TABLE_MSDS);
                if (oldProduct != null) {
                    oldProduct.setUpdatedTime(new Date());
                    oldProduct.setGoodsNumber(product1.getGoodsNumber());
                    oldProduct.setGoodsName(product1.getGoodsName());
                    oldProduct.setGoodsEnglishName(product1.getGoodsEnglishName());
                    oldProduct.setCasNumber(product1.getCasNumber());
                    oldProduct.setUnNumber(product1.getUnNumber());
                    oldProduct.setDangerousGoodsNumber(product1.getDangerousGoodsNumber());
                    oldProduct.setGoodsDesc(product1.getGoodsDesc());
                    productService.updateIchemistryProductMsds(oldProduct);
                } else {
                    int id = productService.saveIchemistryProductMsds(product1);
                }
            });
        }

        Product msdsProduct = resultItems.get("msdsProduct");
        if (msdsProduct != null) {
            Product oldProduct = productService.findByUrlByTableName(msdsProduct.getGoodsUrl(), TABLE_MSDS);
            if (oldProduct != null) {
                oldProduct.setUpdatedTime(new Date());
                oldProduct.setGoodsNumber(msdsProduct.getGoodsNumber());
                oldProduct.setGoodsName(msdsProduct.getGoodsName());
                oldProduct.setGoodsEnglishName(msdsProduct.getGoodsEnglishName());
                oldProduct.setUnNumber(msdsProduct.getUnNumber());
                oldProduct.setCasNumber(msdsProduct.getCasNumber());
                oldProduct.setDangerousGoodsNumber(msdsProduct.getDangerousGoodsNumber());
                oldProduct.setGoodsDesc(msdsProduct.getGoodsDesc());
                productService.updateIchemistryProductMsds(oldProduct);
            } else {
                int id = productService.saveIchemistryProductMsds(msdsProduct);
            }
        }

        List<Product> dangerProducts = resultItems.get("dangerProducts");
        if (dangerProducts != null) {
            dangerProducts.forEach(product1 -> {
                Product oldProduct = productService.findByUrlByTableName(product1.getGoodsUrl(), TABLE_DANGER);
                if (oldProduct != null) {
                    oldProduct.setUpdatedTime(new Date());
                    oldProduct.setDangerousGoodsNumber(product1.getDangerousGoodsNumber());
                    oldProduct.setCat(product1.getCat());
                    oldProduct.setGoodsDesc(product1.getGoodsDesc());
                    oldProduct.setGoodsName(product1.getGoodsName());
                    oldProduct.setGoodsEnglishName(product1.getGoodsEnglishName());
                    oldProduct.setCasNumber(product1.getCasNumber());
                    productService.updateIchemistryProductDanger(oldProduct);
                } else {
                    int id = productService.saveIchemistryProductDanger(product1);
                }
            });
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

        Integer noVipNum = resultItems.get("noVipPdfNum");
        if (noVipNum != null) {
            ProductFile p = ProductFile.builder().goodsFileType("noVipPdf").status("all").build();
            List<ProductFile> productFileList = productService.findProductFile(p, FILE_TABLE);
            if (noVipNum > productFileList.size())
                for (int i = productFileList.size() + 1; i < noVipNum; i++) {
                    String url = "http://www.ichemistry.cn/msds/pdf/" + i + ".pdf";
                    ProductFile productFile = ProductFile.builder().goodsFileType("noVipPdf").goodsFileUrl(url)
                            .goodsUrl("http://www.ichemistry.cn/msds/" + i + ".htm").build();
                    productService.addFileByTableName(productFile, FILE_TABLE);

                }

        }

    }

    private void saveProduct(Product product) {

    }

}
