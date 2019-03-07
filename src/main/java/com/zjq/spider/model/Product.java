package com.zjq.spider.model;

import lombok.*;

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
@ToString
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
     * 产品英文名
     */
    private String goodsEnglishName;

    /**
     * 产品别名
     */
    private String goodsAlias;

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
     * CAS号
     */
    private String casNumber;

    /**
     * 研制单位
     */
    private String developmentUnit;

    /**
     * 分子结构
     */
    private String molecularStructure;

    /**
     * 分子式
     */
    private String molecularFormula;

    /**
     * 分子量
     */
    private String molecularWeight;

    /**
     * 存入字表的参数
     */
    private List<Map<String ,String>> paramList;

}
