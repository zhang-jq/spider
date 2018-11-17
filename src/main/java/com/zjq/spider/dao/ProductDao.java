package com.zjq.spider.dao;

import com.zjq.spider.model.Product;
import com.zjq.spider.model.ProductParam;
import org.apache.ibatis.annotations.Param;

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
}
