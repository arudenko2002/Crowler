package com.umg.ers.analytics.util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Config {
	private static Config instance;
    private PropertiesConfiguration configure;

    private Config() throws ConfigurationException {
        configure = new PropertiesConfiguration("spotify-download-spec-setup.properties");
    }

    public static Config getInstance() {
        if (null == instance) {
            try {
                instance = new Config();
            } catch (ConfigurationException ex) {
                throw new RuntimeException(ex);
            }
        }
        return instance;
    }

    public PropertiesConfiguration getConfigure() {
        return configure;
    }

    public void setConfig(PropertiesConfiguration configure) {
        this.configure = configure;
    }
}