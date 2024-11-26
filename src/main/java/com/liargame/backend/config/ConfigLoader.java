package com.liargame.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ConfigLoader {
    public static Config loadConfig(String resourcePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("리소스를 찾을 수 없습니다: " + resourcePath);
            }
            return objectMapper.readValue(inputStream, Config.class);
        } catch (IOException e) {
            throw new RuntimeException("JSON 설정 파일을 읽는 중 오류 발생: " + e.getMessage(), e);
        }
    }

}
