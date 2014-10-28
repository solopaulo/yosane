package au.com.twobit.yosane.service.op.delivery;

import java.io.File;
import java.util.Collection;

import com.google.common.collect.Lists;

public class PassthroughArtifactCreator implements ArtifactCreator {

    @Override
    public Collection<File> generateArtifactsFromSourceFiles(Collection<File> sourceFiles) {
        return sourceFiles == null ? Lists.<File>newArrayList() : sourceFiles;
    }

}
