package au.com.twobit.yosane.service.op.delivery;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.twobit.yosane.service.send.SendFiles;
import au.com.twobit.yosane.service.storage.Storage;

import com.google.common.collect.Lists;
import com.google.inject.assistedinject.Assisted;

public class ContentDeliveryImpl implements ContentDelivery {
    private static final Logger log = LoggerFactory.getLogger(ContentDeliveryImpl.class);
    
    private Storage storage;
    private String imageOutputFormat;
    private SendFiles sendFiles;
    private ArtifactCreator artifactCreator;
    
    private Map<String,String> deliverySettings;
    private String [] imageIdentifiers;
    
    @Inject
    public ContentDeliveryImpl(Storage storage, 
                               @Named("imageOutputFormat") String imageOutputFormat,
                               @Assisted SendFiles sendFiles,
                               @Assisted String [] imageIdentifiers,
                               @Assisted Map<String,String> deliverySettings,
                               @Assisted ArtifactCreator artifactCreator) {
        this.storage = storage;
        this.imageOutputFormat = imageOutputFormat;
        
        this.sendFiles = sendFiles;        
        this.imageIdentifiers = imageIdentifiers;
        this.deliverySettings = deliverySettings;
        this.artifactCreator = artifactCreator;
    }

    
    
    /** Generates a local files to be used for processing
     * 
     *  When we work with a Storage interface we can't assume it is local so we copy the images
     *  to temporary files so we can work with them directly.
     *  
     *  The list of written temporary files is returned in a collection
     *  
     * @param imageIdentifiers The list of image identifiers that will be retrieved from a Storage interface implementation
     * @return A List of File objects that were written to local file locations
     */
    @Override
    public Collection<File> generateLocalSourceFiles(String[] imageIdentifiers) {
        List<File> tempFiles = Lists.newArrayList();
        if ( imageIdentifiers == null || imageIdentifiers.length == 0 ) {
            return tempFiles;
        }
        
        for (String imageIdentifier : imageIdentifiers) {
            try {
                BufferedImage image = storage.loadImage(imageIdentifier);
                File tempFile = File.createTempFile(imageIdentifier, String.format(".%s", imageOutputFormat));
                if (!tempFile.canWrite()) {
                    log.error("Unable to write the image to a temporary file for emailing: {}", tempFile.getName());
                    continue;
                }
                ImageIO.write(image, imageOutputFormat, tempFile);
                tempFiles.add(tempFile);
            } catch (Exception x) {
                log.error("Failed to write temporary file for image: {}", imageIdentifier);
            }
        }
        return tempFiles;
    }
    

    /** Cleans up local files used for processing
     * 
     * @param sourceFiles
     * @return
     */
    @Override
    public boolean cleanUpLocalSourceFiles(Collection<File> sourceFiles) {
        boolean success = true;
        for ( File tempFile : sourceFiles ) {
            success &= tempFile.delete();
        }
        return success;
    }

    @Override
    public void run() {
        // create local source files
        Collection<File> sourceFiles = generateLocalSourceFiles(imageIdentifiers);
        
        // generate artifacts for delivery
        Collection<File> artifacts = Lists.newArrayList();
        if ( artifactCreator != null ) {
            try {
                artifacts.addAll( artifactCreator.generateArtifactsFromSourceFiles(sourceFiles) );
            } catch (Exception x) {
                log.error("Unable to process artifacts satisfactorily: "+x.getMessage());
            }
        }
        
        // deliver the content
        try {
            sendFiles.sendFilesTo(deliverySettings, artifacts.toArray( new File[]{}));
        } catch (Exception e) {
            log.error("An error occurred sending files: "+e.getMessage());
        }
        
        // create a unique set for cleanup of source files and artifacts
        Set<File> cleanupFiles = new HashSet<File>();
        cleanupFiles.addAll(sourceFiles);
        cleanupFiles.addAll(artifacts);
        
        // remove the artifacts and source files
        cleanUpLocalSourceFiles(cleanupFiles);
    }
}
