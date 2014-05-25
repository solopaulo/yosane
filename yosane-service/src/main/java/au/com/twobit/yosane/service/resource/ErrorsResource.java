package au.com.twobit.yosane.service.resource;

import static com.theoryinpractise.halbuilder.api.RepresentationFactory.HAL_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import au.com.twobit.yosane.api.ErrorCode;
import au.com.twobit.yosane.service.resource.annotations.Relation;
import au.com.twobit.yosane.service.utils.ErrorLookup;

import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.api.Representation;

@Path("/help")
@Relation(relation="help")
public class ErrorsResource {
    public static final String METHOD_ERROR_HELP = "GET ERROR HELP";
    private DefaultRepresentationFactory hal;
    private ErrorLookup lookup;
    
    public ErrorsResource(DefaultRepresentationFactory hal, ErrorLookup lookup) {
        this.hal = hal;
        this.lookup = lookup;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Relation(relation="error",method=METHOD_ERROR_HELP)
    @Path("/{errorCode}")
    public Response errorHelp(@PathParam("errorCode") String errorCode) {
        Representation response = 
                hal.newRepresentation( UriBuilder.fromResource(getClass()).build())
                   .withBean(new ErrorCode(errorCode,lookup.getDescription(errorCode)))
                   // hard coding to avoid potential for recursive calls to this method 
                   .withLink("home", "/");
        return Response.ok( response.toString( HAL_JSON ) ).build();
    }
}
