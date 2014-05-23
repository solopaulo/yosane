package au.com.twobit.yosane.service.utils;

import java.util.Properties;

public class ErrorLookup {
    private Properties props;
    public ErrorLookup() {
        props = new Properties();
        try {
            props.load( ErrorLookup.class.getResourceAsStream("/error.properties") );
        } catch (Exception x) { }
    }
    
    
    public String getDescription(String errorCode) {
        return props.getProperty(errorCode);
    }
}
