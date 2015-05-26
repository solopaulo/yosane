package au.com.twobit.yosane.service.send;

import java.io.File;
import java.util.Map;

/**
 * Sends images to a support output destination (e.g. by email, local file, ssh)
 * 
 * @param files One to many files to be sent to an output. Files may be images, documents, etc.
 * @author paul
 *
 */
public interface SendFiles {
    public static final String NAMING = "NAMING";
    public void sendFilesTo(Map<String,String> settings, File ... files) throws Exception;
    public String getDestinationDescription();
}
