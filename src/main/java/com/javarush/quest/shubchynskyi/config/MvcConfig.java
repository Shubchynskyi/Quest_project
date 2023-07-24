package com.javarush.quest.shubchynskyi.config;

import com.javarush.quest.shubchynskyi.util.constant.ResourceHandlersConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(ResourceHandlersConstants.IMAGES_RESOURCE_HANDLER)
                .addResourceLocations(ResourceHandlersConstants.IMAGES_RESOURCE_LOCATIONS);
        registry.addResourceHandler(ResourceHandlersConstants.ICO_RESOURCE_HANDLER)
                .addResourceLocations(ResourceHandlersConstants.ICO_RESOURCE_LOCATIONS);
    }
}
