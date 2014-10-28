package au.com.twobit.yosane.service.op.delivery;

import java.io.File;
import java.util.Collection;

public interface ArtifactCreator {
    public Collection<File> generateArtifactsFromSourceFiles(Collection<File> sourceFiles) throws Exception;
}
