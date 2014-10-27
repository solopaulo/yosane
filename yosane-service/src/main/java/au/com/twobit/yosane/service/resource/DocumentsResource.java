package au.com.twobit.yosane.service.resource;

import static com.theoryinpractise.halbuilder.api.RepresentationFactory.HAL_JSON;

import java.net.URI;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import au.com.twobit.yosane.api.DocumentOfImages;
import au.com.twobit.yosane.api.Image;
import au.com.twobit.yosane.api.ImageStatus;
import au.com.twobit.yosane.service.resource.annotations.Relation;
import au.com.twobit.yosane.service.storage.Storage;
import au.com.twobit.yosane.service.storage.StorageException;
import au.com.twobit.yosane.service.utils.TicketGenerator;

import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.api.Link;
import com.theoryinpractise.halbuilder.api.Representation;

@Path("/yosane/documents")
@Relation(relation="documents")
public class DocumentsResource {
    private final String GET_DOCUMENT           = "GET DOCUMENT";
    private final String POST_DOCUMENT          = "POST_DOCUMENT";
    private final String ERROR_GET_DOCUMENT     = "error_documents_get";
    private final String ERROR_POST_DOCUMENT    = "error_documents_post";
    
    private DefaultRepresentationFactory hal;
    private TicketGenerator ticketGenerator;
    private Storage storage;
    
    @Inject
    public DocumentsResource(Storage storage, TicketGenerator ticketGenerator, DefaultRepresentationFactory hal) {
        this.hal = hal;
        this.ticketGenerator = ticketGenerator;
        this.storage = storage;
    }
    
    @GET
    @Path("/{documentIdentifier}")
    @Relation(relation="document",method=GET_DOCUMENT)
    public Response getDocument(@PathParam("documentIdentifier") String documentIdentifier) {
        Link self =  ResourceHelper.createLink(getClass(), GET_DOCUMENT, documentIdentifier); 
        Representation response = hal.newRepresentation(self.getHref());
        Link home = ResourceHelper.createLink(HomeResource.class);
        response.withLink( home.getRel(), home.getHref());
        try {
            DocumentOfImages document = storage.loadDocument(documentIdentifier);
            response.withProperty( self.getRel(), document);
        } catch (StorageException x) {
            return ResourceHelper.generateErrorResponse(response, ERROR_GET_DOCUMENT, x.getMessage());
        }
        return Response.ok( response.toString( HAL_JSON ) ).build();
    }
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Relation(relation="document",method=POST_DOCUMENT)
    public Response postDocument(@Valid DocumentOfImages document) {
        Representation response = hal.newRepresentation();
        try {
            // check validity of images
            for ( Image image : document.getImages() ) {
                storage.assertImageStatus(image.getIdentifier(), ImageStatus.READY);
            }
            // get new identifier
            String documentIdentifier = ticketGenerator.newTicket();
            // create link for self
            Link self =  ResourceHelper.createLink(getClass(), GET_DOCUMENT,documentIdentifier);
            response.withLink("self",self.getHref());
            // save the document
            storage.saveDocument(document, documentIdentifier);
            // reload the document
            DocumentOfImages reloaded = storage.loadDocument(documentIdentifier);
            // add created document properties to response
            response.withProperty(self.getRel(), reloaded);
            return Response.created(new URI(self.getHref())).entity( response.toString( HAL_JSON)).build();
        } catch (Exception x) {
            x.printStackTrace();
            return ResourceHelper.generateErrorResponse(response, ERROR_POST_DOCUMENT, x.getMessage());
        }        
    }
}