package com.zjq.spider.dao;

import com.zjq.spider.model.Product;
import com.zjq.spider.model.ProductFile;
import com.zjq.spider.model.ProductParam;
import com.zjq.spider.model.ProductUrl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductDao {

    int save(Product product);

    Product findByUrl(@Param("goodsUrl") String goodsUrl);

    int update(Product product);

    /**
     * 根据表名保存
     * @param product
     * @param tableName
     * @return
     */
    int saveByTableName(@Param("product") Product product, @Param("tableName") String tableName);

    /**
     * 根据表名更新
     * @param product
     * @param tableName
     * @return
     */
    int updateByTableName(@Param("product") Product product, @Param("tableName") String tableName);

    /**
     *
     * @param goodsUrl
     * @return
     */
    Product findByUrlByTableName(@Param("goodsUrl") String goodsUrl, @Param("tableName") String tableName);

    /**
     * 添加参数
     * @param productParam
     * @param tableName
     * @return
     */
    int addParamByTableName(@Param("productParam") ProductParam productParam, @Param("tableName") String tableName);

    /**
     * 根据表名删除产品参数，添加前先删除
     * @param productId
     * @param tableName
     * @return
     */
    int deleteParamByTableName(@Param("productId") Integer productId, @Param("tableName") String tableName);

    /**
     * 添加文件
     * @param productFile
     * @param tableName
     * @return
     */
    int addFileByTableName(@Param("productFile") ProductFile productFile, @Param("tableName") String tableName);

    /**
     * 获取文件
     * @param productFile
     * @param tableName
     * @return
     */
    List<ProductFile> findProductFile(@Param("productFile") ProductFile productFile, @Param("tableName") String tableName);

    /**
     * 修改文件信息
     * @param productFile
     * @param tableName
     * @return
     */
    int updateProductFile(@Param("productFile") ProductFile productFile, @Param("tableName") String tableName);

    /**
     * 保存chemistryProduct
     * @param product
     * @return
     */
    int saveIchemistryProduct(@Param("product") Product product);

    /**
     * 更新chemistryProduct
     * @param product
     * @return
     */
    int updateIchemistryProduct(@Param("product") Product product);

    /**
     * 保存chemistryProduct的MSDS
     * @param product
     * @return
     */
    int saveIchemistryProductMsds(@Param("product") Product product);

    /**
     * 更新chemistryProduct的MSDS
     * @param product
     * @return
     */
    int updateIchemistryProductMsds(@Param("product") Product product);

    /**
     * 保存chemistryProduct的危險化学品
     * @param product
     * @return
     */
    int saveIchemistryProductDanger(@Param("product") Product product);

    /**
     * 更新chemistryProduct的的危險化学品
     * @param product
     * @return
     */
    int updateIchemistryProductDanger(@Param("product") Product product);

    /**
     * 保存产品链接
     * @param productUrl
     * @return
     */
    int saveProductUrl(@Param("productUrl") ProductUrl productUrl);

    /**
     * 获取还没抓取产品的链接
     * @return
     */
    List<ProductUrl> findNotSpiderProductUrl(@Param("productUrl") ProductUrl productUrl, @Param("tableName") String tableName);



}
