package com.javarush.quest.shubchynskyi.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@ConfigurationProperties(prefix = "app.images")
public class ImageProperties {

    private final String noImageFilename;
    private final List<String> allowedExtensions;
    private final List<String> allowedMimeTypes;
    private final String defaultMimeType;
    private final Map<String, String> extensionToMimeType;
    private final int maxFileSize;
    private final long timeToDeleteInMillis;
    private final String prefixForTempImages;

}