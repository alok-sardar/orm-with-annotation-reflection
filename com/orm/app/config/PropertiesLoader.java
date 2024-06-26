package com.orm.app.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    public Properties loadProperties() throws IOException {
        Properties properties = new Properties();

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("com/orm/app/resource/application.properties");
        properties.load(inputStream);
        return properties;
    }
}
