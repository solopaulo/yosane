package au.com.twobit.yosane.service.guice;

import java.util.concurrent.ExecutorService;

import au.com.twobit.yosane.service.dw.EmailConfiguration;
import au.com.twobit.yosane.service.dw.YosaneServiceConfiguration;
import au.com.twobit.yosane.service.image.ImageFormat;
import au.com.twobit.yosane.service.op.command.CreateThumbnail;
import au.com.twobit.yosane.service.send.SendFiles;
import au.com.twobit.yosane.service.send.provider.SendFilesEmailACE;
import au.com.twobit.yosane.service.storage.ArtifactCleanup;
import au.com.twobit.yosane.service.storage.FileStorage;
import au.com.twobit.yosane.service.storage.FileStorageArtifactCleanup;
import au.com.twobit.yosane.service.storage.Storage;
import au.com.twobit.yosane.service.utils.EncodeDecode;
import au.com.twobit.yosane.service.utils.TicketGenerator;
import au.com.twobit.yosane.service.utils.URLEncodeDecode;
import au.com.twobit.yosane.service.utils.UUIDTicketGenerator;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.json.JsonRepresentationFactory;

public class YosaneGuiceModule extends AbstractModule {

    final private ExecutorService executorService;
    final private YosaneServiceConfiguration configuration;
    
    public YosaneGuiceModule(ExecutorService executorService, YosaneServiceConfiguration configuration) {
        this.executorService = executorService;
        this.configuration = configuration;
    }

    @Provides
    public ExecutorService getExecutorService() {
        return executorService;
    }
    @Override
    protected void configure() {
        configureHalBuilder();
        configureMiscellany();     
        configureYosaneSettings();
        configureEmail();
//        install( new SaneDependencyModule() );
        
        MockSaneDependencyModule m = new MockSaneDependencyModule();
        requestInjection(m);
        install( m );
    }

    private void configureYosaneSettings() {
        // set up temporary holding area location
        bind(String.class).annotatedWith(Names.named("holdingArea")).toInstance("/tmp/yosane/");
        // set up output image format
        bind(String.class).annotatedWith(Names.named("imageOutputFormat")).toInstance(ImageFormat.png.name());
        // register the file storage class for image persistence
        bind(Storage.class).to(FileStorage.class);
        bind(String.class).annotatedWith(Names.named("staleTime")).toInstance("1d");
        bind(ArtifactCleanup.class).to(FileStorageArtifactCleanup.class);
        // set the default thumbnail scale length (width)
        requestStaticInjection(CreateThumbnail.class);
        bind(Integer.class).annotatedWith(Names.named("scaleWidth")).toInstance(180);        
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
        jsonFactory.withFlag( DefaultRepresentationFactory.SINGLE_ELEM_ARRAYS);
        bind(DefaultRepresentationFactory.class).toInstance( jsonFactory );        
   }

   private void configureEmail() {
       bind(EmailConfiguration.class).toInstance( configuration.getEmailConfiguration());
       bind(SendFiles.class).to(SendFilesEmailACE.class);
   }
}
