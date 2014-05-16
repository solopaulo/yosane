package au.com.twobit.yosane.service.resource;

import static au.com.twobit.yosane.service.image.ImageFormat.png;
import static au.com.twobit.yosane.service.image.ImageUtils.createByteArrayFromImage;
import io.dropwizard.jersey.caching.CacheControl;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import au.com.twobit.yosane.api.ImageStatus;
import au.com.twobit.yosane.service.command.ImageRotation;
import au.com.twobit.yosane.service.image.RotateDirection;
import au.com.twobit.yosane.service.storage.Storage;

import com.google.common.base.Optional;
import com.google.inject.Inject;

@Path("/images")
public class ImagesResource {

    private Storage storage;
    private ExecutorService executorService;

    @Inject
    public ImagesResource(Storage storage, ExecutorService executorService) {
        this.storage = storage;
        this.executorService = executorService;
    }

    @GET
    @Path("/{imageId}")
    public Response getImageDetails(@PathParam("imageId") String imageIdentifier) throws Exception {
        ImageStatus status = storage.getStatus(imageIdentifier);
        return Response.ok(status.name()).build();
    }

    @GET
    @Path("/{imageId}/file")
    @Produces("image/png")
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.MINUTES)
    public Response getImageFile(@PathParam("imageId") String imageIdentifier) {
        try {
            storage.assertStatus(imageIdentifier, ImageStatus.READY);
            BufferedImage image = storage.loadImage(imageIdentifier);
            return Response.ok().entity(createByteArrayFromImage(image, png.name())).build();
        } catch (Exception x) {
            return Response.serverError().build();
        }
    }
    
    
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

    @POST
    @Path("/{imageId}/rotate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rotateImageFile(@PathParam("imageId") String imageIdentifier, @QueryParam("direction") String rotation) {
        RotateDirection direction = RotateDirection.UNKNOWN;
        try {
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
