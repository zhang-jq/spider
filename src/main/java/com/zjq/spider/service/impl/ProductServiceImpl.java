package com.zjq.spider.service.impl;

import com.zjq.spider.dao.ProductDao;
import com.zjq.spider.model.Product;
import com.zjq.spider.model.ProductFile;
import com.zjq.spider.model.ProductParam;
import com.zjq.spider.service.ProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductDao productDao;

    @Override
    public int save(Product product) {
        return productDao.save(product);
    }

    @Override
    public Product findByUrl(String goodsUrl) {
        return productDao.findByUrl(goodsUrl);
    }

    @Override
    public int update(Product product) {
        return productDao.update(product);
    }

    @Override
    public int saveByTableName(Product product, String tableName) {
        productDao.saveByTableName(product, tableName);
        return product.getGoodsId();
    }

    @Override
    public int updateByTableName(Product product, String tableName) {
        return productDao.updateByTableName(product, tableName);
    }

    @Override
    public Product findByUrlByTableName(String goodsUrl, String tableName) {
        return productDao.findByUrlByTableName(goodsUrl, tableName);
    }

    @Override
    public int addParamByTableName(ProductParam productParam, String tableName) {
        return productDao.addParamByTableName(productParam, tableName);
    }

    @Override
    public int deleteParamByTableName(Integer productId, String tableName) {
        return productDao.deleteParamByTableName(productId, tableName);
    }

    @Override
    public int addFileByTableName(ProductFile productFile, String tableName) {
        return productDao.addFileByTableName(productFile, tableName);
    }

    @Override
    public List<ProductFile> findProductFile(ProductFile productFile, String tableName) {
        return productDao.findProductFile(productFile, tableName);
    }

    @Override
    public int updateProductFile(ProductFile productFile, String tableName) {
        return productDao.updateProductFile(productFile, tableName);
    }

    @Override
    public int saveIchemistryProduct(Product product) {
        return productDao.saveIchemistryProduct(product);
    }

    @Override
    public int updateIchemistryProduct(Product product) {
        return productDao.updateIchemistryProduct(product);
    }

    @Override
    public int saveIchemistryProductMsds(Product product) {
        return productDao.saveIchemistryProductMsds(product);
    }

    @Override
    public int updateIchemistryProductMsds(Product product) {
        return productDao.updateIchemistryProductMsds(product);
    }

    @Override
    public int saveIchemistryProductDanger(Product product) {
        return productDao.saveIchemistryProductDanger(product);
    }

    @Override
    public int updateIchemistryProductDanger(Product product) {
        return productDao.updateIchemistryProductDanger(product);
    }
}
