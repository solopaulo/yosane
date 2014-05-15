package au.com.twobit.yosane.service.resource;

import io.dropwizard.jersey.caching.CacheControl;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.twobit.yosane.api.DeviceOption;
import au.com.twobit.yosane.api.Image;
import au.com.twobit.yosane.api.ImageStatus;
import au.com.twobit.yosane.service.command.ScanImage;
import au.com.twobit.yosane.service.device.ScanHardware;
import au.com.twobit.yosane.service.storage.Storage;
import au.com.twobit.yosane.service.utils.TicketGenerator;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import com.google.inject.Inject;


/** A Resource to make scanning requests to available scanners
 * 
 * @author paul
 *
 */
@Path("/scanners")
public class ScannersResource {
	private Logger log = LoggerFactory.getLogger(getClass());
	private final String UNKNOWN_SCANNER_IDENTIFIER	= "This is not a valid scanner identifier";
	/* an interface used to talk to scanner hardware */
	final private ScanHardware hardware;
	/* an interface used to manage image persistence requirements */
	final private Storage storage;
	/* a simple id generator */
	final private TicketGenerator ticketGenerator;
	/* executor service */
	final private ExecutorService executorService;
	
	@Inject
	public ScannersResource(ScanHardware hardware,Storage storage, TicketGenerator ticketGenerator, ExecutorService executorService) {
		this.hardware = hardware;
		this.storage = storage;
		this.ticketGenerator = ticketGenerator;
		this.executorService = executorService;
	}

	
	/** Gets the list of scanners available from this host
	 * 
	 * @return List of scanners 
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.MINUTES)
	public Response getScannerList() {
		try {
			return Response.ok(hardware.getListOfScanDevices()).build();
		} catch (Exception x) {
			log.error("Failed to get a list of scanner hardware devices: {}",x.getMessage());
			return Response.serverError().build();
		}
	}

	
	/** Gets the details of a single scanner
	 * 
	 */
	@GET
	@Path("/{scannerId}")
	@Produces(MediaType.APPLICATION_JSON)
	@CacheControl(maxAge=1,maxAgeUnit=TimeUnit.MINUTES)
	public Response getScanner(@PathParam("scannerId") String scannerId) {
		try {
			return Response.ok( hardware.getScanDeviceDetails(Base64Coder.decodeString(scannerId))).build();
		} catch (Exception x) {
			// log an error
			x.printStackTrace();
		}
		return Response.serverError().build();
	}
	
	
	/** Acquires a new image and returns meta data about it
	 * 
	 */
	@Path("/{scannerId}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response acquireImage(@PathParam("scannerId") String scannerId, @Context HttpServletRequest request) {
		// create an image descriptor
		Image image = new Image();
		image.setStatus(ImageStatus.ACCEPTED);
		image.setIdentifier( ticketGenerator.newTicket() );
		URI location = null;
		
		// dispatch a scanning request
		String scannerName = null;
		try {
			scannerName = Base64Coder.decodeString(scannerId);
			
			location = UriBuilder.fromPath("/images/"+image.getIdentifier()+"/download").build(image.getIdentifier());
		} catch (Exception x) { }
		executorService.execute( new ScanImage(hardware,storage,scannerName, image.getIdentifier()) );
		
		// return the response
		return Response.ok(image).location( location ).build();
	}
	
	
	/** Gets scanner options that can be set
	 * 
	 */
	@Path("/{scannerId}/options")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.MINUTES)
	public Response getScannerOptions(@PathParam("scannerId") String scannerId) {
		String error = "";
		try {
			List<DeviceOption>options = hardware.getScanDeviceOptions( Base64Coder.decodeString(scannerId) );
			return Response.ok(options).build();
		} catch (IllegalArgumentException x) {
			error =  UNKNOWN_SCANNER_IDENTIFIER;
		} catch (Exception x) {
			error = x.getMessage();
		}
		log.error(error);
		return Response.serverError().build();
	}
	
	@Path("/{scannerId}/options")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response setScannerOptions(@PathParam("scannerId") String scannerId) {
		String error = "";
		try {
			
		} catch (IllegalArgumentException x) {
			error = UNKNOWN_SCANNER_IDENTIFIER;
		}
		return Response.serverError().build();
	}
	
	static enum Relations {
		SCANNER("Scanning device"),
		SCANNERS("List of Scanning devices");
		
		final private String description;
		private Relations(String description) {
			this.description = description;
		}
	}
}
