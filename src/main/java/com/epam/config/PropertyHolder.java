package com.epam.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The {@code PropertyHolder} class is responsible for loading application properties from a properties file.
 * It provides a static {@code Properties} object that holds the loaded properties.
 * <p>
 * The properties are loaded from a file named {@code application.properties} located in the classpath.
 * </p>
 */
public class PropertyHolder {
    /**
     * A static {@code Properties} object that holds the application properties loaded from the properties file.
     */
    public static Properties properties = new Properties();

    // Static block to initialize the properties from the properties file
    static {
        try (InputStream input = PropertyHolder.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find application.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
