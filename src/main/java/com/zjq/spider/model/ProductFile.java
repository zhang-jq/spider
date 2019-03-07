package com.zjq.spider.model;

import lombok.*;

import java.util.Date;

/**
 * 产品文件
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductFile {

    private Integer id;

    /**
     * 产品id
     */
    private Integer goodsId;

    /**
     * 产品连接
     */
    private String goodsUrl;

    /**
     * 产品文件连接
     */
    private String goodsFileUrl;

    /**
     * 产品文件类型
     */
    private String goodsFileType;

    /**
     * 状态 如已下载 下载失败
     */
    private String status;


    private Date updatedTime;

    private Date createdTime;

}
