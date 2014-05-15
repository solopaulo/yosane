package au.com.twobit.yosane.service.resource;

import static au.com.twobit.yosane.service.image.ImageFormat.png;
import static au.com.twobit.yosane.service.image.ImageUtils.createByteArrayFromImage;
import io.dropwizard.jersey.caching.CacheControl;

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
import au.com.twobit.yosane.service.storage.Storage;

import com.google.inject.Inject;

@Path("/images")
public class ImagesResource {

	private Storage storage;
	
	@Inject
	public ImagesResource(Storage storage) {
		this.storage = storage;
	}
	
	@GET
	@Path("/{imageId}")
	public Response getImageDetails(@PathParam("imageId") String imageIdentifier) throws Exception {
		ImageStatus status = storage.getStatus(imageIdentifier);
		return Response.ok(status.name()).build();
	}
	
	@GET
	@Path("/{imageId}/download")
	@Produces("image/png")
	@CacheControl(maxAge=1,maxAgeUnit=TimeUnit.MINUTES)
	public Response getImageFile(@PathParam("imageId") String imageId, @QueryParam("size") String size) {
		try { 
			return Response
					.ok()
					.entity( createByteArrayFromImage(storage.loadImage(imageId), png.name()) )
					.build();
		} catch (Exception x) { x.printStackTrace(); }
		return Response.ok().build();            
	}

	
	@POST
	@Path("/{imageId}/rotate")
	@Produces(MediaType.APPLICATION_JSON)
	public Response rotateImageFile(@QueryParam("direction") String rotation) {
		
		return Response.serverError().build();
	}
	
}
