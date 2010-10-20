/**
 * ApplicationProperties.java
 * @author Vagisha Sharma
 * Aug 18, 2009
 * @version 1.0
 */
package org.yeastrc.jqs.queue.ws;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 */
public class ApplicationProperties {

    private final static Logger log = Logger.getLogger(ApplicationProperties.class);
    private static String noreplySender = "";
    
    public static void load() {
        
        String propsFile = "application.properties";
        System.out.println("LOADING PROPERTIES FROM: "+propsFile);
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(propsFile));
            noreplySender = props.getProperty("noreply.sender");
        }
        catch (FileNotFoundException e) {
            log.error("Properties file: "+propsFile+" not found!", e);
            e.printStackTrace();
        }
        catch (IOException e) {
            log.error("Error reading properties file: "+propsFile, e);
            e.printStackTrace();
        }
    }

    public static String getNoreplySender() {
        return noreplySender;
    }
}
