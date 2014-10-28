package au.com.twobit.yosane.service.resource;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
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
import au.com.twobit.yosane.service.resource.dto.LocalFileMessage;
import au.com.twobit.yosane.service.send.provider.SendFilesEmail;
import au.com.twobit.yosane.service.send.provider.SendFilesLocalDir;
import au.com.twobit.yosane.service.storage.Storage;

import com.google.common.collect.Maps;

@Path("/yosane/send")
@Relation(relation="send")
public class SendResource {
    private ExecutorService executorService;
    private ContentDeliveryFactory deliveryFactory;
    private PdfArtifactCreator pdfArtifactCreator;
    
    @Inject
    public SendResource(Storage storage, 
                        ExecutorService executorService,
                        ContentDeliveryFactory deliveryFactory,
                        PdfArtifactCreator pdfArtifactCreator) {
        this.executorService = executorService;
        this.deliveryFactory = deliveryFactory;
        this.pdfArtifactCreator = pdfArtifactCreator;
    }
    
    @POST
    @Path("/email/image")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendEmailImage(@Valid EmailMessage emailMessage) {
        return createEmailResponse(emailMessage, new PassthroughArtifactCreator());
    }
    
    @POST
    @Path("/email/pdf")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendEmailPdf(@Valid EmailMessage emailMessage) {
        return createEmailResponse(emailMessage, pdfArtifactCreator);
    }

    @POST
    @Path("/localfile/image")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendLocalfileImage(@Valid LocalFileMessage localFileMessage) {
        return createLocalFileResponse(localFileMessage, new PassthroughArtifactCreator());
    }
    
    @POST
    @Path("/localfile/pdf")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendLocalfilePdf(@Valid LocalFileMessage localFileMessage) {
        return createLocalFileResponse(localFileMessage, pdfArtifactCreator);
    }
    
    Response createEmailResponse(EmailMessage emailMessage, ArtifactCreator artifactCreator) {
        Map<String,String> settings = Maps.newHashMap();
        settings.put(SendFilesEmail.RECIPIENT, emailMessage.getRecipient());
        ContentDelivery delivery = deliveryFactory.create( emailMessage.getImageIdentifiers(), settings, artifactCreator);
        executorService.execute( delivery );
        return Response.ok().build();
    }
    
    Response createLocalFileResponse(LocalFileMessage localFileMessage, ArtifactCreator artifactCreator) {
        Map<String,String> settings = Maps.newHashMap();
        settings.put(SendFilesLocalDir.LOCAL_DIR, localFileMessage.getLocalPath()); 
        ContentDelivery delivery = deliveryFactory.create( localFileMessage.getImageIdentifiers(), settings, artifactCreator);
        executorService.execute(delivery);
        return Response.ok().build();
    }

}
