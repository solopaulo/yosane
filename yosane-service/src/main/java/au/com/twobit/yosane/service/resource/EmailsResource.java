package au.com.twobit.yosane.service.resource;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import au.com.twobit.yosane.service.op.command.EmailImage;
import au.com.twobit.yosane.service.resource.annotations.Relation;
import au.com.twobit.yosane.service.resource.dto.EmailMessage;
import au.com.twobit.yosane.service.storage.Storage;

@Path("/email")
@Relation(relation="emails")
public class EmailsResource {
    private ExecutorService executorService;
    private EmailImage emailImage;
    
    @Inject
    public EmailsResource(Storage storage, ExecutorService executorService, EmailImage emailImage) {
        this.executorService = executorService;
        this.emailImage = emailImage;
    }
    
    @POST
    @Path("/image")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendImageEmail(EmailMessage emailMessage) {
        if ( emailMessage == null || 
                emailMessage.getImageIdentifiers() == null ||
                emailMessage.getImageIdentifiers().length == 0 ) {
            return Response.serverError().entity("That is bad").build();
        }        
        executorService.execute(emailImage.send( emailMessage ) );
        return Response.ok().build();
    }
}
