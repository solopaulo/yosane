package au.com.twobit.yosane.service.op.command;

import java.io.ByteArrayOutputStream;
import java.io.File;

import javax.inject.Named;

import au.com.twobit.yosane.service.image.ImageFormat;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.PngImage;

public class CreatePDFDocument {

    private final String imageOutputFormat;
    
    public CreatePDFDocument(@Named("imageOutputFormat") String imageOutputFormat) {
        this.imageOutputFormat = imageOutputFormat;
    }
    
    public byte[] generatePdfData(File [] imageFiles) throws Exception {
        if ( imageFiles == null || imageFiles.length == 0 ) {
            return new byte[]{};
        }
        
        // create a new PDF document that will write out to a byte array output stream
        Document document = new Document();
        ByteArrayOutputStream pdfData = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, pdfData);
        document.open();
        // add each image as a new page in the pdf
        for (File imageFile : imageFiles) {
            try {
                // create a new PDF page
                
                writer.newPage();
                // add the image to the page
                document.add( createPdfImageFromFile( imageFile ) );
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
        // close pdf
        try {
            document.close();
        } catch (Exception x) {
            x.printStackTrace();
        }
        return pdfData.toByteArray();
    }
    
    Image createPdfImageFromFile( File imageFile ) throws Exception {
        if ( ImageFormat.png.name().equals( imageOutputFormat ) ) {
            return PngImage.getImage( imageFile.getPath() ) ;
        }
        
        throw new Exception("Currently only handling PNG image types");
    }
}
