package com.zjq.spider.service;

import com.zjq.spider.model.Product;
import com.zjq.spider.model.ProductFile;
import com.zjq.spider.model.ProductParam;
import com.zjq.spider.model.ProductUrl;

import java.util.List;

public interface ProductService {

    int save(Product product);

    Product findByUrl(String goodsUrl);

    int update(Product product);


    /**
     * 根据表名保存
     * @param product
     * @param tableName
     * @return
     */
    int saveByTableName(Product product, String tableName);

    /**
     * 根据表名更新
     * @param product
     * @param tableName
     * @return
     */
    int updateByTableName(Product product, String tableName);

    /**
     *
     * @param goodsUrl
     * @return
     */
    Product findByUrlByTableName(String goodsUrl, String tableName);

    /**
     * 添加参数
     * @param productParam
     * @param tableName
     * @return
     */
    int addParamByTableName(ProductParam productParam, String tableName);

    /**
     * 根据表名删除产品参数，添加前先删除
     * @param productId
     * @param tableName
     * @return
     */
    int deleteParamByTableName(Integer productId, String tableName);

    /**
     * 添加文件
     * @param productFile
     * @param tableName
     * @return
     */
    int addFileByTableName(ProductFile productFile, String tableName);

    /**
     * 获取文件
     * @param productFile
     * @param tableName
     * @return
     */
    List<ProductFile> findProductFile(ProductFile productFile, String tableName);

    /**
     * 修改文件信息
     * @param productFile
     * @param tableName
     * @return
     */
    int updateProductFile(ProductFile productFile, String tableName);

    /**
     * 保存chemistryProduct
     * @param product
     * @return
     */
    int saveIchemistryProduct(Product product);

    /**
     * 更新chemistryProduct
     * @param product
     * @return
     */
    int updateIchemistryProduct(Product product);


    /**
     * 保存chemistryProduct的MSDS
     * @param product
     * @return
     */
    int saveIchemistryProductMsds(Product product);

    /**
     * 更新chemistryProduct的MSDS
     * @param product
     * @return
     */
    int updateIchemistryProductMsds(Product product);

    /**
     * 保存chemistryProduct的危險化学品
     * @param product
     * @return
     */
    int saveIchemistryProductDanger(Product product);

    /**
     * 更新chemistryProduct的的危險化学品
     * @param product
     * @return
     */
    int updateIchemistryProductDanger(Product product);


    /**
     * 保存产品链接
     * @param productUrl
     * @return
     */
    int saveProductUrl(ProductUrl productUrl);

    /**
     * 获取还没抓取产品的链接
     * @return
     */
    List<ProductUrl> findNotSpiderProductUrl(ProductUrl productUrl, String tableName);

}
