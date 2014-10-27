package au.com.twobit.yosane.service.resource;

import static au.com.twobit.yosane.service.resource.ImagesResource.METHOD_GET_IMAGE_DETAILS;
import static au.com.twobit.yosane.service.resource.ResourceHelper.createLink;
import static au.com.twobit.yosane.service.resource.ResourceHelper.createRelation;
import static com.theoryinpractise.halbuilder.api.RepresentationFactory.HAL_JSON;
import io.dropwizard.jersey.caching.CacheControl;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
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
import au.com.twobit.yosane.service.device.ScanHardware;
import au.com.twobit.yosane.service.image.ImageUtils;
import au.com.twobit.yosane.service.op.command.ScanImage;
import au.com.twobit.yosane.service.resource.annotations.Relation;
import au.com.twobit.yosane.service.resource.dto.ScanMessage;
import au.com.twobit.yosane.service.storage.Storage;
import au.com.twobit.yosane.service.utils.EncodeDecode;
import au.com.twobit.yosane.service.utils.TicketGenerator;

import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.api.Link;
import com.theoryinpractise.halbuilder.api.Representation;

/**
 * A Resource to make scanning requests to available scanners
 * 
 * @author paul
 * 
 */
@Path("/yosane/scanners")
@Relation(relation="scanners")
public class ScannersResource {
    final static String METHOD_GET_SCANNER = "GET SCANNER";
    final static String METHOD_GET_OPTIONS = "GET OPTIONS";
    
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final String ERROR_SCANNER_OPTIONS  = "error_scanner_options";
    private final String ERROR_SCANNER_ACQUIRE  = "error_scanner_acquire";
    private final String ERROR_SCANNER_DETAIL   = "error_scanner_detail";
    private final String ERROR_SCANNER_LIST     = "error_scanner_list";

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
        Representation response = hal.newRepresentation( createLink(ScannersResource.class).getHref() );
        try {
            List<Device>scanningDevices = hardware.getListOfScanDevices();
            for (Device device : scanningDevices) {
                response.withLink(
                        "scanner",
                        createLink(ScannersResource.class,METHOD_GET_SCANNER,device.getId()).getHref(),
                        device.getName(),
                        String.format("%s %s", device.getVendor(),device.getModel()),
                        null,
                        null);
            }
            response.withProperty("scanners", scanningDevices);
        } catch (Exception x) {
            String error = String.format("Failed to get a list of scanner hardware devices: %s", x.getMessage()); 
            log.error(error);
            return ResourceHelper.generateErrorResponse(response, ERROR_SCANNER_LIST,error);
        }
        return Response.ok(response.toString( HAL_JSON)).build();
    }
    
    
    /**
     * Gets the details of a single scanner
     * 
     */
    @GET
    @Path("/{scannerId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.MINUTES)
    @Relation(relation="scanner",method=METHOD_GET_SCANNER)
    public Response getScanner(@PathParam("scannerId") String scannerId) {
        Representation response = 
                hal.newRepresentation(createLink(ScannersResource.class,METHOD_GET_SCANNER,scannerId).getHref());
        try {
            Device scanner = hardware.getScanDeviceDetails(coder.decodeString(scannerId));
            Link scanHomeLink = createLink(ScannersResource.class);
            Link scanOptionsLink = createLink(ScannersResource.class,ScannersResource.METHOD_GET_OPTIONS,scannerId);
            response.withBean(scanner)
                    .withLink(scanHomeLink.getRel(),scanHomeLink.getHref())
                    .withLink(scanOptionsLink.getRel(),scanOptionsLink.getHref());
        } catch (Exception x) {
            // log an error
            String error = String.format("Failed to get scanner details: %s",x.getMessage());
            log.error(error);
            ResourceHelper.generateErrorResponse(response, ERROR_SCANNER_DETAIL, error);
        }
        
        return Response.ok( response.toString(HAL_JSON )).build();
    }
     

    /**
     * Acquires a new image and returns meta data about it
     * 
     */
    @Path("/{scannerId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response acquireImage(@PathParam("scannerId") String scannerId, ScanMessage scanMessage) {
        // create an image descriptor
        String iid = null;
        Image image = ImageUtils.createImageWithTicket( (iid = ticketGenerator.newTicket() ) );
        URI location = null;
        Class<?>ir = ImagesResource.class;
        // build a response
        Representation response = hal.newRepresentation( createLink(ir, METHOD_GET_IMAGE_DETAILS,iid).getHref());
        
        // dispatch a scanning request
        String scannerName = null;
        try {
            scannerName = coder.decodeString(scannerId);
            executorService.execute(new ScanImage(hardware, storage, scannerName, iid,scanMessage.getDeviceOptions()));
            location = new URI(createLink(ir, METHOD_GET_IMAGE_DETAILS, iid).getHref());
        } catch (Exception x) {
            return ResourceHelper.generateErrorResponse(response, ERROR_SCANNER_ACQUIRE, x.getMessage());
        }
        response
            .withLink(createRelation(ScannersResource.class), 
                      createLink(ScannersResource.class).getHref())
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
    @Relation(relation="options",method=METHOD_GET_OPTIONS)
    public Response getScannerOptions(@PathParam("scannerId") String scannerId) {
        Representation response = hal.newRepresentation( createLink(ScannersResource.class,METHOD_GET_OPTIONS, scannerId).getHref());        
        try {
            List<DeviceOption> options = hardware.getScanDeviceOptions(coder.decodeString(scannerId));
            response.withProperty("options",options);
        } catch (Exception x) {
            return ResourceHelper.generateErrorResponse(response, ERROR_SCANNER_OPTIONS, x.getMessage());
        }
        return Response.ok(response.toString( HAL_JSON ) ).build();
    }
}