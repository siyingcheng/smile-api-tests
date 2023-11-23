package com.smile.core.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
@Slf4j
public class FileUtils {

    public void createDirectory(Path path) {
        try {
            Files.createDirectories(path);
            log.info("Directory {} created", path);
        } catch (IOException e) {
            log.error("Create {} error:", path, e);
        }
    }
}
