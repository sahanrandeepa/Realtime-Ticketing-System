package com.ticketmanagementsystem.pos.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketmanagementsystem.pos.dto.ConfigDTO;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class FileHandler {

    private final ObjectMapper objectMapper;

    public FileHandler() {
        this.objectMapper = new ObjectMapper();
    }

    public void saveConfigToFile(ConfigDTO configDTO, String fileName) throws IOException {
        File file = new File(fileName);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, configDTO);
    }

    public ConfigDTO readConfigFromFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }
        return objectMapper.readValue(file, ConfigDTO.class);
    }
}