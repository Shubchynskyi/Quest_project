package com.javarush.quest.shubchynskyi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    @Value("${app.resources.images.handler}")
    private String imagesResourceHandler;

    @Value("${app.resources.images.locations}")
    private String imagesResourceLocations;

    @Value("${app.resources.images.temp.handler}")
    private String imagesTempResourceHandler;

    @Value("${app.resources.images.temp.locations}")
    private String imagesTempResourceLocations;

    @Value("${app.resources.icons.handler}")
    private String icoResourceHandler;

    @Value("${app.resources.icons.locations}")
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