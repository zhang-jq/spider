package com.zjq.spider.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产品参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductParam {

    private Integer id;

    /**
     * 产品参数
     */
    private Integer productId;

    private String name;

    private String value;
}
