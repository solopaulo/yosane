package au.com.twobit.yosane.service.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.twobit.yosane.service.utils.InformalPeriod;

public class FileStorageArtifactCleanup implements ArtifactCleanup {

    private Logger log = LoggerFactory.getLogger(getClass());
    private String holdingArea;
    private String staleTime;

    @Inject
    public FileStorageArtifactCleanup(@Named("holdingArea") String holdingArea,
                                      @Named("staleTime") String staleTime) {
        this.holdingArea = holdingArea;
        this.staleTime = staleTime;
    }
    
    @Override
    public void run() {
        final Path path = Paths.get( new File(holdingArea).toURI() );
        final long ts = InformalPeriod.subtractPeriodFromDate(staleTime, DateTime.now()).getMillis();
        
        try {
            java.nio.file.Files.walkFileTree( path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException arg1) throws IOException {
                    if ( dir.toFile().list().length == 0 && ! dir.equals( path ) ) {
                       java.nio.file.Files.delete(dir);
                       log.info("Deleted stale directory {}",dir.toFile().getPath());
                    }
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes arg1) throws IOException {                    
                    File df = file.toFile();
                    if ( df.lastModified() < ts ) {
                        java.nio.file.Files.delete(file);
                        log.info("Deleted stale file{}",df.getPath());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception x) {
            log.error("Encountered an error during cleanup: {}",x.getMessage());
        }
    }

}
