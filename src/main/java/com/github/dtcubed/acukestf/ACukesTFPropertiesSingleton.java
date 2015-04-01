package com.github.dtcubed.acukestf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

// This Singleton leverages the "Bill Pugh" singleton example presented here:
// http://howtodoinjava.com/2012/10/22/singleton-design-pattern-in-java/
public class ACukesTFPropertiesSingleton {

    boolean localDebug = false;

    // This could be JSON, YAML, XML, Properties, INI, etc.
    // Right now, we are choosing to use the Java Properties mechanisms.
    // Properties examples:
    // http://www.mkyong.com/java/java-properties-file-examples/
    // http://en.wikipedia.org/wiki/.properties
    Properties prop;

    private ACukesTFPropertiesSingleton() {

        InputStream input = null;

        // String testPropertiesFile = System.getenv("PN_TEST_PROPERTIES");
        // TODO: take out the hard-code.
        String acukestfPropertiesFile = "support/properties/acukestf.properties";

        if (localDebug) {
            System.out.println("Test Properties File thought to be: [" + acukestfPropertiesFile + "]");
        }

        try {

            input = new FileInputStream(acukestfPropertiesFile);

            prop = new Properties();

            // load a properties file
            prop.load(input);

            if (localDebug) {
                System.out.println("============= START ===================");
                System.out.println(prop.getProperty("database"));
                System.out.println("============= END =====================");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class LazyHolder {
        private static final ACukesTFPropertiesSingleton INSTANCE = new ACukesTFPropertiesSingleton();
    }

    public static ACukesTFPropertiesSingleton getInstance() {
        return LazyHolder.INSTANCE;
    }

    public String getPropertyValue(String propertyName) {
        return prop.getProperty(propertyName);
    }
}








