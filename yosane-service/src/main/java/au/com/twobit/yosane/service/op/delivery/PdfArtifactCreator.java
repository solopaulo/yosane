package au.com.twobit.yosane.service.op.delivery;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.UUID;

import javax.inject.Inject;

import au.com.twobit.yosane.service.op.command.CreatePDFDocument;

import com.google.common.collect.Lists;

public class PdfArtifactCreator implements ArtifactCreator {
    private CreatePDFDocument pdfCreator;
    
    @Inject
    public PdfArtifactCreator(CreatePDFDocument pdfCreator) {
        this.pdfCreator = pdfCreator;
    }

    @Override
    public Collection<File> generateArtifactsFromSourceFiles(Collection<File> sourceFiles) throws Exception {
        File pdfFile = File.createTempFile(UUID.randomUUID().toString(), ".pdf");
        // add the image to the page
        FileOutputStream fos = new FileOutputStream(pdfFile);
        fos.write( pdfCreator.generatePdfData( sourceFiles.toArray(new File[] {}) ));
        fos.close();
        
        return Lists.newArrayList(pdfFile);
    }

}
