package com.zjq.spider.pipeline;

import com.zjq.spider.model.Product;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * User: zjq
 * Date: 19-3-7
 * Time: 上午11:48
 * Description: http://www.ichemistry.cn
 */
@Component
public class IchemistryPipeline implements Pipeline {
    @Override
    public void process(ResultItems resultItems, Task task) {
        Product product = resultItems.get("product");
    }
}
