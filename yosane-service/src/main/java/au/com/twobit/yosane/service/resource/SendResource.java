package au.com.twobit.yosane.service.resource;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import au.com.twobit.yosane.service.op.command.EmailImage;
import au.com.twobit.yosane.service.op.command.LocalFileImage;
import au.com.twobit.yosane.service.resource.annotations.Relation;
import au.com.twobit.yosane.service.resource.dto.EmailMessage;
import au.com.twobit.yosane.service.resource.dto.LocalFileMessage;
import au.com.twobit.yosane.service.storage.Storage;

@Path("/send")
@Relation(relation="send")
public class SendResource {
    private ExecutorService executorService;
    private EmailImage emailImage;
    private LocalFileImage localFileImage;
    
    @Inject
    public SendResource(Storage storage, ExecutorService executorService, 
                        EmailImage emailImage, 
                        LocalFileImage localFileImage) {
        this.executorService = executorService;
        this.emailImage = emailImage;
        this.localFileImage = localFileImage;
    }
    
    @POST
    @Path("/email/image")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendEmailImage(EmailMessage emailMessage) {
        if ( emailMessage == null 
                || emailMessage.getImageIdentifiers() == null
                || emailMessage.getImageIdentifiers().length == 0 ) {
            return Response.serverError().entity("That is bad").build();
        }        
        executorService.execute(emailImage.send( emailMessage ) );
        return Response.ok().build();
    }

    @POST
    @Path("/localfile/image")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendLocalfileImage(LocalFileMessage localFileMessage) {
        if ( localFileMessage == null 
                || localFileMessage.getImageIdentifiers() == null
                || localFileMessage.getImageIdentifiers().length == 0 ) {
            return Response.serverError().entity("That is bad").build();
        }        
        executorService.execute(localFileImage.send( localFileMessage ) );
        return Response.ok().build();
    }

}
