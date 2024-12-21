package com.javarush.quest.shubchynskyi.test_config;

import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Getter
public class TestImageProperties {

    private final String noImageFilename;
    private final List<String> allowedExtensions;
    private final List<String> allowedMimeTypes;
    private final String defaultMimeType;
    private final Map<String, String> extensionToMimeType;
    private final int maxFileSize;
    private final long timeToDeleteInMillis;
    private final String prefixForTempImages;

    @SuppressWarnings("unchecked")
    public TestImageProperties() {
        Yaml yaml = new Yaml();

        try (InputStream in = Files.newInputStream(Paths.get("src/main/resources/application.yaml"))) {
            Map<String, Object> obj = yaml.load(in);
            Map<String, Object> appConfig = (Map<String, Object>) obj.get("app");
            Map<String, Object> imagesConfig = (Map<String, Object>) appConfig.get("images");

            this.noImageFilename = (String) imagesConfig.get("no-image-filename");
            this.allowedExtensions = (List<String>) imagesConfig.get("allowed-extensions");
            this.allowedMimeTypes = (List<String>) imagesConfig.get("allowed-mime-types");
            this.defaultMimeType = (String) imagesConfig.get("default-mime-type");
            this.extensionToMimeType = (Map<String, String>) imagesConfig.get("extension-to-mime-type");
            this.maxFileSize = (int) imagesConfig.get("max-file-size");
            this.timeToDeleteInMillis = ((Number) imagesConfig.get("time-to-delete-in-millis")).longValue();
            this.prefixForTempImages = (String) imagesConfig.get("prefix-for-temp-images");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image properties from application.yaml", e);
        }
    }

}