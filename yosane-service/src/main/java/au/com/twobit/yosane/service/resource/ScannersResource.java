package au.com.twobit.yosane.service.resource;

import static au.com.twobit.yosane.service.resource.ImagesResource.METHOD_GET_IMAGE_FILE;
import static au.com.twobit.yosane.service.resource.ResourcePathBuilder.createPath;
import static au.com.twobit.yosane.service.resource.ResourcePathBuilder.createRelation;
import static com.theoryinpractise.halbuilder.api.RepresentationFactory.HAL_JSON;
import io.dropwizard.jersey.caching.CacheControl;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.twobit.yosane.api.Device;
import au.com.twobit.yosane.api.DeviceOption;
import au.com.twobit.yosane.api.Image;
import au.com.twobit.yosane.api.ImageStatus;
import au.com.twobit.yosane.service.device.ScanHardware;
import au.com.twobit.yosane.service.op.command.ScanImage;
import au.com.twobit.yosane.service.resource.annotations.Relation;
import au.com.twobit.yosane.service.storage.Storage;
import au.com.twobit.yosane.service.utils.EncodeDecode;
import au.com.twobit.yosane.service.utils.TicketGenerator;

import com.google.inject.Inject;
import com.sun.jersey.core.provider.EntityHolder;
import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.api.Representation;

/**
 * A Resource to make scanning requests to available scanners
 * 
 * @author paul
 * 
 */
@Path("/scanners")
@Relation("scanners")
public class ScannersResource {
    final static String METHOD_GET_SCANNER = "GET SCANNER";
    
    private Logger log = LoggerFactory.getLogger(getClass());
    private final String UNKNOWN_SCANNER_IDENTIFIER = "This is not a valid scanner identifier";
    /* an interface used to talk to scanner hardware */
    final private ScanHardware hardware;
    /* an interface used to manage image persistence requirements */
    final private Storage storage;
    /* a simple id generator */
    final private TicketGenerator ticketGenerator;
    /* executor service */
    final private ExecutorService executorService;
    /* json factory for hateoas */
    final DefaultRepresentationFactory hal;
    /* encoder / decoder */
    final EncodeDecode coder;
    
    @Inject
    public ScannersResource(ScanHardware hardware,
                            Storage storage,
                            TicketGenerator ticketGenerator,
                            ExecutorService executorService,
                            DefaultRepresentationFactory hal,
                            EncodeDecode coder) {
        this.hardware = hardware;
        this.storage = storage;
        this.ticketGenerator = ticketGenerator;
        this.executorService = executorService;
        this.hal = hal;
        this.coder = coder;
    }
    
    
    
    /** Gets a list of all available scanners
     * 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.MINUTES)
    public Response getScannerList() {
        try {
            Representation response = hal.newRepresentation( createPath(ScannersResource.class).get() );
            for (Device device : hardware.getListOfScanDevices()) {
                response.withLink(
                        "scanner",
                        createPath(ScannersResource.class,METHOD_GET_SCANNER,device.getId()).get().toString(),
                        device.getName(),
                        String.format("%s %s", device.getVendor(),device.getModel()),
                        null,
                        null);
            }
            return Response.ok(response.toString( HAL_JSON)).build();
        } catch (Exception x) {
            log.error("Failed to get a list of scanner hardware devices: {}", x.getMessage());
            return Response.serverError().build();
        }
    }
    
    
    /**
     * Gets the details of a single scanner
     * 
     */
    @GET
    @Path("/{scannerId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.MINUTES)
    @Named(METHOD_GET_SCANNER)
    @Relation("scanner")
    public Response getScanner(@PathParam("scannerId") String scannerId) {
        try {
            Device scanner = hardware.getScanDeviceDetails(coder.decodeString(scannerId));
            Representation response = 
                    hal.newRepresentation(createPath(ScannersResource.class,METHOD_GET_SCANNER,scannerId).get())
                        .withBean(scanner)
                        .withLink(createRelation(ScannersResource.class),
                                  createPath(ScannersResource.class).get());                    
            return Response.ok( response.toString(HAL_JSON )).build();
        } catch (Exception x) {
            // log an error
            x.printStackTrace();
        }
        return Response.serverError().build();

    }
     

    /**
     * Acquires a new image and returns meta data about it
     * 
     */
    @Path("/{scannerId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response acquireImage(@PathParam("scannerId") String scannerId,
                                 @Valid List<DeviceOption> options) {
        // create an image descriptor
        Image image = new Image();
        image.setStatus(ImageStatus.ACCEPTED);
        String iid = ticketGenerator.newTicket();
        image.setIdentifier(iid);
        URI location = null;

        // dispatch a scanning request
        String scannerName = null;
        try {
            scannerName = coder.decodeString(scannerId);
            Class<?>ir = ImagesResource.class;
            location = createPath(ir, METHOD_GET_IMAGE_FILE, image.getIdentifier()).or( HomeResource.HOME );
        } catch (Exception x) {
        }
        executorService.execute(new ScanImage(hardware, storage, scannerName, image.getIdentifier(),options));
        
        // build a response
        Class<?> ir = ImagesResource.class;
        
        Representation response = 
                hal.newRepresentation(
                        createPath(ir, METHOD_GET_IMAGE_FILE,iid).get())
                   .withLink(createRelation(ScannersResource.class), 
                        createPath(ScannersResource.class).get())
                   .withBean(image);
        return Response.ok(response.toString( HAL_JSON) ).location(location).build();
    }
    

    /**
     * Gets scanner options that can be set
     * 
     */
    @Path("/{scannerId}/options")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.MINUTES)
    public Response getScannerOptions(@PathParam("scannerId") String scannerId) {
        String error = "";
        try {
            List<DeviceOption> options = hardware.getScanDeviceOptions(coder.decodeString(scannerId));
            return Response.ok(options).build();
        } catch (IllegalArgumentException x) {
            error = UNKNOWN_SCANNER_IDENTIFIER;
        } catch (Exception x) {
            error = x.getMessage();
        }
        log.error(error);
        return Response.serverError().build();
    }

   
}