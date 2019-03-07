package com.zjq.spider.service;

import com.zjq.spider.model.Product;
import com.zjq.spider.model.ProductFile;
import com.zjq.spider.model.ProductParam;

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

}
