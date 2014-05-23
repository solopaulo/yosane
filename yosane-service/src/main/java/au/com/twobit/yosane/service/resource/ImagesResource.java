package au.com.twobit.yosane.service.resource;

import static au.com.twobit.yosane.service.image.ImageFormat.png;
import static au.com.twobit.yosane.service.image.ImageUtils.createByteArrayFromImage;
import io.dropwizard.jersey.caching.CacheControl;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import au.com.twobit.yosane.api.Image;
import au.com.twobit.yosane.api.ImageStatus;
import au.com.twobit.yosane.service.image.ImageFormat;
import au.com.twobit.yosane.service.image.RotateDirection;
import au.com.twobit.yosane.service.op.command.ImageRotation;
import au.com.twobit.yosane.service.storage.Storage;

import com.google.inject.Inject;
import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.api.Representation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;

@Path("/images")
public class ImagesResource {
    public static final String METHOD_GET_IMAGE_FILE = "GET IMAGE FILE";
    
    private Storage storage;
    private ExecutorService executorService;
    private DefaultRepresentationFactory hal;
    
    @Inject
    public ImagesResource(Storage storage, ExecutorService executorService, DefaultRepresentationFactory hal) {
        this.storage = storage;
        this.executorService = executorService;
        this.hal = hal;
    }

    /* (non-Javadoc)
     * @see au.com.twobit.yosane.service.resource.impl.ImagesResource#getImageDetails(java.lang.String)
     */
    @GET
    @Path("/{imageId}")
    public Response getImageDetails(@PathParam("imageId") String imageIdentifier) throws Exception {
        ImageStatus status = storage.getStatus(imageIdentifier);
        Image image = new Image();
        image.setIdentifier(imageIdentifier);
        image.setOutputFormat( ImageFormat.png.name() );
        image.setStatus(status);
        
        String pathbase = String.format("%s/%s",getClass().getAnnotation(Path.class).value(),imageIdentifier);
        
        Representation response = 
                hal.newRepresentation(pathbase)
                   .withLink("scan", "/scanners")
                   .withLink("image-rotate", String.format("%s/rotate",pathbase))
                   .withLink("image-download", String.format("%s/file",pathbase))
                   .withLink("image-download-thumb", String.format("%s/file/thumb",pathbase))
                   .withBean(image);
        return Response.ok(response.toString( RepresentationFactory.HAL_JSON)).build();
    }

    /* (non-Javadoc)
     * @see au.com.twobit.yosane.service.resource.impl.ImagesResource#getImageFile(java.lang.String)
     */
    @GET
    @Path("/{imageId}/file")
    @Produces("image/png")
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.MINUTES)
    @Named(METHOD_GET_IMAGE_FILE)
    public Response getImageFile(@PathParam("imageId") String imageIdentifier) {
        try {
            storage.assertStatus(imageIdentifier, ImageStatus.READY);
            BufferedImage image = storage.loadImage(imageIdentifier);
            return Response.ok().entity(createByteArrayFromImage(image, png.name())).build();
        } catch (Exception x) {
            return Response.serverError().build();
        }
    }
    
    
    /* (non-Javadoc)
     * @see au.com.twobit.yosane.service.resource.impl.ImagesResource#getImageThumb(java.lang.String)
     */
    @GET
    @Path("/{imageId}/file/thumb")
    @Produces("image/png")
    public Response getImageThumb(@PathParam("imageId") String imageIdentifier) {
        try {
            storage.assertStatus(imageIdentifier, ImageStatus.READY);
            BufferedImage image = storage.loadImageThumbnail(imageIdentifier);
            return Response.ok().entity(createByteArrayFromImage(image, png.name())).build();
        } catch (Exception x) {
            return Response.serverError().build();
        }
    }

    /* (non-Javadoc)
     * @see au.com.twobit.yosane.service.resource.impl.ImagesResource#rotateImageFile(java.lang.String, java.lang.String)
     */
    @POST
    @Path("/{imageId}/rotate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rotateImageFile(@PathParam("imageId") String imageIdentifier, @QueryParam("direction") String rotation) {
        try {
            RotateDirection direction = RotateDirection.UNKNOWN;
            storage.assertStatus(imageIdentifier, ImageStatus.READY);
            direction = RotateDirection.valueOf(rotation.toUpperCase());
            if ( direction == RotateDirection.UNKNOWN ) {
                throw new Exception();
            }
            executorService.execute( new ImageRotation(storage, imageIdentifier,direction) );
            return Response.ok().build();
        } catch (Exception x) { 
            x.printStackTrace();
            
        }
        return Response.serverError().build();
    }

}
