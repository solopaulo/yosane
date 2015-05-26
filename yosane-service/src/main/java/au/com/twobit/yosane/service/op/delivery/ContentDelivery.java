package au.com.twobit.yosane.service.op.delivery;

import java.io.File;
import java.util.Collection;

public interface ContentDelivery extends Runnable {
    
    
    /** Generates a local files to be used for processing
     * 
     *  When we work with a Storage interface we can't assume it is local so we copy the images
     *  to temporary files so we can work with them directly.
     *  
     *  The list of written temporary files is returned in a collection
     *  
     * @param imageIdentifiers The list of image identifiers that will be retrieved from a Storage interface implementation
     * @param naming An optional naming string that may be used in a naming scheme
     * @return A List of File objects that were written to local file locations
     */
    public Collection<File> generateLocalSourceFiles(String [] imageIdentifiers, String naming);
    
    
    /** Cleans up local files used for processing
     * 
     * @param sourceFiles
     * @return
     */
    public boolean cleanUpLocalSourceFiles(Collection<File> allFiles);
}
