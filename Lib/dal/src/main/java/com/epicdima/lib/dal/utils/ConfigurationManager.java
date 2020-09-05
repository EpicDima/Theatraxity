package com.epicdima.lib.dal.utils;

import com.epicdima.lib.dal.exceptions.ConfigurationManagerException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author EpicDima
 */
public abstract class ConfigurationManager extends Properties {

    public ConfigurationManager(String propertiesFileName) {
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(propertiesFileName)) {
            load(inputStream);
        } catch (IOException e) {
            throw new ConfigurationManagerException(e);
        }
    }
}
