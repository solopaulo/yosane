package au.com.twobit.yosane.service.resource;

import static com.theoryinpractise.halbuilder.api.RepresentationFactory.HAL_JSON;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import au.com.twobit.yosane.api.DocumentOfImages;
import au.com.twobit.yosane.service.resource.annotations.Relation;
import au.com.twobit.yosane.service.storage.Storage;
import au.com.twobit.yosane.service.storage.StorageException;

import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.api.Link;
import com.theoryinpractise.halbuilder.api.Representation;

@Path("/documents")
@Relation(relation="documents")
public class DocumentsResource {
    private final String GET_DOCUMENT = "GET DOCUMENT";
    private final String ERROR_GET_DOCUMENT = "error_documents_get";
    private DefaultRepresentationFactory hal;
    private Storage storage;
    
    @Inject
    public DocumentsResource(Storage storage, DefaultRepresentationFactory hal) {
        this.hal = hal;
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
}