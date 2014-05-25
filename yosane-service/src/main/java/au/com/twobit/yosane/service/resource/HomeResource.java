package au.com.twobit.yosane.service.resource;

import java.net.URI;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import au.com.twobit.yosane.service.resource.annotations.Relation;

import com.google.inject.Inject;
import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.api.Representation;

@Path("/")
@Relation(relation="home")
public class HomeResource {
    final static URI HOME = UriBuilder.fromResource(HomeResource.class).build();
    public static final String METHOD_HOME_API      =   "HOME API";
    private DefaultRepresentationFactory hal;
    
    @Inject
    public HomeResource(DefaultRepresentationFactory hal) {
        this.hal = hal;
    }

    /* (non-Javadoc)
     * @see au.com.twobit.yosane.service.resource.impl.HomeResource#apiHome()
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Relation(relation="home",method=METHOD_HOME_API)
    public Response apiHome() {
        Representation response = hal.newRepresentation("/")
                .withLink("scanners", ScannersResource.class.getAnnotation(Path.class).value())
                .withLink("images",ImagesResource.class.getAnnotation(Path.class).value())
                .withLink("documents", DocumentsResource.class.getAnnotation(Path.class).value());
        return Response.ok( response.toString( DefaultRepresentationFactory.HAL_JSON ) ).build();
    }
}
