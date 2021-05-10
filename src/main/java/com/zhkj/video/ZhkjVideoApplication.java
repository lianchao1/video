package com.zhkj.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import com.iron.base.utils.ApplicationContextUtil;

//dubbo相关
//@EnableDubbo
//@ImportResource("classpath:dubbo/zhkj-provider-service.xml")
@Import({ApplicationContextUtil.class})
@ComponentScan({"com.iron.base", "com.zhkj.video"})
@SpringBootApplication
public class ZhkjVideoApplication {
    private final static Logger logger = LoggerFactory.getLogger(ZhkjVideoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ZhkjVideoApplication.class, args);
        logger.info("视频处理启动完成！");
    }

}
