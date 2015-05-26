package au.com.twobit.yosane.service.resource;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import au.com.twobit.yosane.service.op.delivery.ArtifactCreator;
import au.com.twobit.yosane.service.op.delivery.ContentDelivery;
import au.com.twobit.yosane.service.op.delivery.ContentDeliveryFactory;
import au.com.twobit.yosane.service.op.delivery.PassthroughArtifactCreator;
import au.com.twobit.yosane.service.op.delivery.PdfArtifactCreator;
import au.com.twobit.yosane.service.resource.annotations.Relation;
import au.com.twobit.yosane.service.resource.dto.LocalFileMessage;
import au.com.twobit.yosane.service.send.SendFiles;
import au.com.twobit.yosane.service.send.provider.SendFilesLocalDir;
import au.com.twobit.yosane.service.storage.Storage;

import com.google.common.collect.Maps;

@Path("/yosane/send/localfile")
@Relation(relation="send")
public class SendToFileResource {
    private ExecutorService executorService;
    private ContentDeliveryFactory deliveryFactory;
    private PdfArtifactCreator pdfArtifactCreator;
    private PassthroughArtifactCreator passthroughArtifactCreator = new PassthroughArtifactCreator();
    private SendFiles sendFilesLocally;
    
    @Inject
    public SendToFileResource(Storage storage, 
                        ExecutorService executorService,
                        ContentDeliveryFactory deliveryFactory,
                        PdfArtifactCreator pdfArtifactCreator,
                        @Named("sendLocalFile") SendFiles sendFilesLocally) {
        this.executorService = executorService;
        this.deliveryFactory = deliveryFactory;
        this.pdfArtifactCreator = pdfArtifactCreator;
        this.sendFilesLocally = sendFilesLocally;
    }
    
    @POST
    @Path("/image")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendLocalfileImage(@Valid LocalFileMessage localFileMessage) {
        return createLocalFileResponse(localFileMessage, passthroughArtifactCreator);
    }
    
    @POST
    @Path("/pdf")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendLocalfilePdf(@Valid LocalFileMessage localFileMessage) {
        return createLocalFileResponse(localFileMessage, pdfArtifactCreator);
    }

    /** Sends files locally according to the settings, by building artifacts using the creator specified
     * 
     * @param localFileMessage Settings for the local file delivery, to be passed to the SendFiles implementation.
     * @param artifactCreator The implementation of the artifact creator, which generates the files that will be attachments on the email
     * @return Returns a response to send back to the client. As these operations are asynchronous we should really give them a ticket to
     *          check or something
     */
    Response createLocalFileResponse(LocalFileMessage localFileMessage, ArtifactCreator artifactCreator) {
        // creates a new map for settings
        Map<String,String> settings = Maps.newHashMap();
        settings.put(SendFilesLocalDir.LOCAL_DIR, localFileMessage.getLocalPath()); 
        settings.put( SendFiles.NAMING,  localFileMessage.getNaming());
        // get a delivery factory that combines the generation of artifacts with the magic of delivery.
        ContentDelivery delivery = deliveryFactory.create( localFileMessage.getImageIdentifiers(),  settings, sendFilesLocally, artifactCreator);
        executorService.execute(delivery);
        return Response.ok().build();
    }
}
