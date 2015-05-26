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
import au.com.twobit.yosane.service.resource.dto.EmailMessage;
import au.com.twobit.yosane.service.send.SendFiles;
import au.com.twobit.yosane.service.send.provider.SendFilesEmail;
import au.com.twobit.yosane.service.storage.Storage;

import com.google.common.collect.Maps;

@Path("/yosane/send/email")
@Relation(relation="send")
public class SendToEmailResource {
    private ExecutorService executorService;
    private ContentDeliveryFactory deliveryFactory;
    private PdfArtifactCreator pdfArtifactCreator;
    private PassthroughArtifactCreator passthroughArtifactCreator = new PassthroughArtifactCreator();
    private SendFiles sendFilesByEmail;
    
    @Inject
    public SendToEmailResource(Storage storage, 
                        ExecutorService executorService,
                        ContentDeliveryFactory deliveryFactory,
                        PdfArtifactCreator pdfArtifactCreator,
                        @Named("sendEmail") SendFiles sendFilesByEmail) {
        this.executorService = executorService;
        this.deliveryFactory = deliveryFactory;
        this.pdfArtifactCreator = pdfArtifactCreator;
        this.sendFilesByEmail = sendFilesByEmail;
    }
    
    @POST
    @Path("/image")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendEmailImage(@Valid EmailMessage emailMessage) {
        return createEmailResponse(emailMessage, passthroughArtifactCreator);
    }
    
    @POST
    @Path("/pdf")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendEmailPdf(@Valid EmailMessage emailMessage) {
        return createEmailResponse(emailMessage, pdfArtifactCreator);
    }    
    
    /** Sends an email to the recipient by building artifacts using the creator specified
     * 
     * @param emailMessage Settings for the email, to be passed to the SendFiles implementation. Currently only doing recipient
     * @param artifactCreator The implementation of the artifact creator, which generates the files that will be attachments on the email
     * @return Returns a response to send back to the client. As these operations are asynchronous we should really give them a ticket to
     *          check or something
     */
    Response createEmailResponse(EmailMessage emailMessage, ArtifactCreator artifactCreator) {
        // creates a new map for settings
        Map<String,String> settings = Maps.newHashMap();
        settings.put(SendFilesEmail.RECIPIENT, emailMessage.getRecipient());
        settings.put(SendFiles.NAMING, emailMessage.getNaming());
        // get a delivery factory that combines the generation of artifacts with the magic of delivery.
        ContentDelivery delivery = deliveryFactory.create( emailMessage.getImageIdentifiers(), settings,sendFilesByEmail, artifactCreator);
        executorService.execute( delivery );
        return Response.ok().build();
    }
    

}
