package au.com.twobit.yosane.service.send.provider;

import java.io.File;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.joda.time.DateTime;

import au.com.twobit.yosane.api.NotificationType;
import au.com.twobit.yosane.service.dw.config.LocalDirectoryConfiguration;
import au.com.twobit.yosane.service.resource.dto.Notification;
import au.com.twobit.yosane.service.send.SendFiles;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.io.Files;

public class SendFilesLocalDir implements SendFiles {

    private LocalDirectoryConfiguration conf;
    private EventBus eventBus;
    public final static String LOCAL_DIR = "LOCAL_DIR";
    static final SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
    static final String NO_TRAILING_SLASHES = "/*$";
    
    
    @Inject
    public SendFilesLocalDir(LocalDirectoryConfiguration conf,EventBus eventBus) {
        this.conf = conf;
        this.eventBus = eventBus;
    }
    
    @Override
    public void sendFilesTo(Map<String, String> settings, File... files) throws Exception {
        if ( files == null || files.length == 0 ) {
            throw new Exception("No files presented for sending to local directory");
        }

        
        String localDir = Optional.fromNullable(settings.get( LOCAL_DIR ))
                                  .or( conf.getDefaultDirectory() );
                                  
        if ( ! isAcceptablePath( localDir, conf == null ? null : conf.getLocalPaths() ) ) {
            throw new Exception("Not an acceptable output path: "+localDir);
        }
        File outputDirectory = validateLocalPath( new File( localDir) );                                
        
        // if writing to daily directory, ensure it exists and set output directory to it
        boolean writeDailyDirectory = conf == null || conf.isCreateDirectoryForEachDay();
        if ( writeDailyDirectory ) {
            outputDirectory = createDailyDirectory(outputDirectory);
        }
        
        String naming = settings.get( SendFiles.NAMING );
        if ( ! Strings.isNullOrEmpty(naming) ) {
            outputDirectory = createDirectory( outputDirectory, naming );
        }
                
        // write the files to the output directory
        int seq = 1;
        DecimalFormat df = new DecimalFormat(Strings.repeat("0", String.valueOf(files.length).length() + 1));
        
        for ( File outfile : files) {
            String name = outfile.getName();
            
            if ( ! Strings.isNullOrEmpty(naming) ) {
                name = String.format("%s-%s",naming,df.format(seq++));
                name += outfile.getName().substring(outfile.getName().lastIndexOf("."));
            }
            Path copyTo = outputDirectory.toPath().resolve( name );
            Files.copy(outfile, copyTo.toFile() );
            Notification notification = 
                    Notification.create(
                            String.format("Wrote %s to disk",copyTo.toString().replaceAll(localDir,"")),
                            NotificationType.COMPLETED_FILES_COPIED);
            eventBus.post( notification );
                    
        }        
    }

    /** Determines if the path is in the acceptable path list
     * 
     * @param localDir
     * @return Returns true if the path is in our list of accepted paths
     */
    boolean isAcceptablePath(String localDir, Map<String,String> acceptablePathsMap) {
        if ( Strings.isNullOrEmpty( localDir ) || acceptablePathsMap == null ) {
            return false;
        }
        List<String>acceptablePaths = Lists.newArrayList( acceptablePathsMap.values());
        for (String acceptablePath : acceptablePaths ) {
            if ( localDir.replaceAll(NO_TRAILING_SLASHES,"").equals( acceptablePath.replaceAll(NO_TRAILING_SLASHES, "") ) ) {
                return true;
            }
        }
        return false;
    }

    /** Validates the local path exists and can be written to
     * 
     * @param path
     * @return
     * @throws Exception
     */
    File validateLocalPath(File outputDirectory) throws Exception {
        if ( outputDirectory == null ) {
            throw new Exception("No path specified");
        }
        if ( ! (outputDirectory.exists() || outputDirectory.mkdir() )  ) {
            throw new Exception("Cannot create");
        }
        if ( ! outputDirectory.canWrite() ) {
            throw new Exception("Cannot write");
        }
        return outputDirectory;
    }

    /** Creates the daily directory if it does not exist and returns as a File object
     * 
     * @param outputDirectory The base path where the daily directory will exist
     * @return Returns a File object representing the daily directory path
     * @throws Exception thrown if directory did not exist and we could not create it
     */
    File createDailyDirectory(File outputDirectory) throws Exception {
        String daily = sdf.format(DateTime.now().toDate());
        return createDirectory(outputDirectory,daily);
    }
    
    File createDirectory(File outputDirectory,String lastPath) throws Exception {
        Path path = outputDirectory.toPath().resolve( lastPath );
        File dirPath = path.toFile();
        if ( ( ! dirPath.exists()) && ! dirPath.mkdir() ) {
            throw new Exception("Unable to create new path: "+dirPath.getPath());
        }
        return dirPath;
    }
    

    @Override
    public String getDestinationDescription() {
        return "Send to local directory";
    }

}
