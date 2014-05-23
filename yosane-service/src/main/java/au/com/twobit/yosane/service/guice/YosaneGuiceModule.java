package au.com.twobit.yosane.service.guice;

import io.dropwizard.lifecycle.Managed;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import au.com.twobit.yosane.service.image.ImageFormat;
import au.com.twobit.yosane.service.op.command.CreateThumbnail;
import au.com.twobit.yosane.service.storage.FileStorage;
import au.com.twobit.yosane.service.storage.Storage;
import au.com.twobit.yosane.service.utils.EncodeDecode;
import au.com.twobit.yosane.service.utils.TicketGenerator;
import au.com.twobit.yosane.service.utils.URLEncodeDecode;
import au.com.twobit.yosane.service.utils.UUIDTicketGenerator;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.json.JsonRepresentationFactory;

public class YosaneGuiceModule extends AbstractModule {

    public YosaneGuiceModule() {
    }

    @Override
    protected void configure() {
        configureHalBuilder();
        configureMiscellany();     
        configureExecutorService();
        configureYosaneSettings();
        install( new SaneDependencyModule() );
//        
//        MockSaneDependencyModule m = new MockSaneDependencyModule();
//        requestInjection(m);
//        install( m );
    }

    private void configureYosaneSettings() {
        // set up temporary holding area location
        bind(String.class).annotatedWith(Names.named("holdingArea")).toInstance("/tmp/yosane/");
        // set up output image format
        bind(String.class).annotatedWith(Names.named("imageOutputFormat")).toInstance(ImageFormat.png.name());
        // register the file storage class for image persistence
        bind(Storage.class).to(FileStorage.class);
        
        // set the default thumbnail scale length (width)
        requestStaticInjection(CreateThumbnail.class);
        bind(Integer.class).annotatedWith(Names.named("scaleWidth")).toInstance(320);        
    }

    private void configureExecutorService() {
        // start a new executor service for background tasks
        int maxThreads = 3;
        final ExecutorService executorService = Executors.newFixedThreadPool(maxThreads);
        bind(ExecutorService.class).toInstance(executorService);
        bind(Managed.class).annotatedWith(Names.named("async")).toInstance(new Managed() {
            @Override public void start() throws Exception { }
            @Override
            public void stop() throws Exception {
                executorService.shutdown();
            }
        });
    }

    private void configureMiscellany() {
        // register a ticket generator
        bind(TicketGenerator.class).to(UUIDTicketGenerator.class);
        bind(EncodeDecode.class).to(URLEncodeDecode.class);
    }

    private void configureHalBuilder() {
        // add a HAL representation factory to be re-used through resources
        DefaultRepresentationFactory jsonFactory = new JsonRepresentationFactory();
        jsonFactory.withFlag( DefaultRepresentationFactory.PRETTY_PRINT );
        bind(DefaultRepresentationFactory.class).toInstance( jsonFactory );        
   }

}
