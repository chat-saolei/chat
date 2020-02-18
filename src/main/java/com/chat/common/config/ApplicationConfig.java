package com.chat.common.config;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

@Slf4j
public class ApplicationConfig {
    public static int port;
    public static int threadNum;

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File("application.properties")));
            port = Integer.parseInt(properties.getProperty("server.port", "8000"));
            threadNum = Integer.parseInt(properties.getProperty("thread.num", "10"));
        } catch (Exception e) {
            log.error("", e);
            System.exit(0);
        }
    }
}
