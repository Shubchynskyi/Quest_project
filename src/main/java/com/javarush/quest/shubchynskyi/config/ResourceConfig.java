package com.javarush.quest.shubchynskyi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    @Value("${images.resource.handler}")
    private String imagesResourceHandler;

    @Value("${images.resource.locations}")
    private String imagesResourceLocations;

    @Value("${images.resource.temp.handler}")
    private String imagesTempResourceHandler;

    @Value("${images.resource.temp.locations}")
    private String imagesTempResourceLocations;

    @Value("${ico.resource.handler}")
    private String icoResourceHandler;

    @Value("${ico.resource.locations}")
    private String icoResourceLocations;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(imagesResourceHandler)
                .addResourceLocations(imagesResourceLocations);
        registry.addResourceHandler(imagesTempResourceHandler)
                .addResourceLocations(imagesTempResourceLocations);
        registry.addResourceHandler(icoResourceHandler)
                .addResourceLocations(icoResourceLocations);
    }
}
