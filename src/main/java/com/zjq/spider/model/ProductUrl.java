package com.zjq.spider.model;

import lombok.*;

import java.util.Date;

/**
 * @author zhangjiaqiang
 * @Description TODO 产品链接
 * @createTime 2019年03月28日 11:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductUrl {

    private Integer id;

    private String url;

    /**
     * 产品链接所属网站，或网站类型
     */
    private String type;

    private Date updatedTime;

    private Date createdTime;


}
