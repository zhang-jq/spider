package com.zjq.spider.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 产品
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    /**
     * 产品id
     */
    private Integer goodsId;

    /**
     * 产品详情页连接
     */
    private String goodsUrl;

    /**
     * 产品编号
     */
    private String goodsSn;

    /**
     * 产品名称
     */
    private String goodsName;

    /**
     * 分类
     */
    private String cat;

    /**
     * 库存
     */
    private String goodsNumber;

    /**
     * 产品重量（规格存入此中）
     */
    private String goodsWeight;

    /**
     * 销售价格
     */
    private String shopPrice;

    /**
     * 产品详情
     */
    private String goodsDesc;

    /**
     * 产品图片
     */
    private String goodsImg;

    /**
     * 属性 json
     */
    private String params;

    private Date updatedTime;

    private Date createdTime;

    /**
     * 标准值
     * @return
     */
    private String standardValue;

    /**
     * CDS号
     */
    private String cdsNumber;

    /**
     * 研制单位
     */
    private String developmentUnit;

    /**
     * 存入字表的参数
     */
    private List<Map<String ,String>> paramList;

}
