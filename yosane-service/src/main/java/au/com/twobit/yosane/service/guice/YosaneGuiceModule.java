package au.com.twobit.yosane.service.guice;

import java.util.concurrent.ExecutorService;

import javax.inject.Named;

import au.com.twobit.yosane.service.dw.YosaneServiceConfiguration;
import au.com.twobit.yosane.service.dw.config.EmailConfiguration;
import au.com.twobit.yosane.service.dw.config.LocalDirectoryConfiguration;
import au.com.twobit.yosane.service.image.ImageFormat;
import au.com.twobit.yosane.service.op.command.CreatePDFDocument;
import au.com.twobit.yosane.service.op.command.CreateThumbnail;
import au.com.twobit.yosane.service.op.delivery.ContentDelivery;
import au.com.twobit.yosane.service.op.delivery.ContentDeliveryFactory;
import au.com.twobit.yosane.service.op.delivery.ContentDeliveryImpl;
import au.com.twobit.yosane.service.op.delivery.PdfArtifactCreator;
import au.com.twobit.yosane.service.send.SendFiles;
import au.com.twobit.yosane.service.send.provider.SendFilesEmailACE;
import au.com.twobit.yosane.service.send.provider.SendFilesLocalDir;
import au.com.twobit.yosane.service.storage.ArtifactCleanup;
import au.com.twobit.yosane.service.storage.FileStorage;
import au.com.twobit.yosane.service.storage.FileStorageArtifactCleanup;
import au.com.twobit.yosane.service.storage.Storage;
import au.com.twobit.yosane.service.utils.EncodeDecode;
import au.com.twobit.yosane.service.utils.TicketGenerator;
import au.com.twobit.yosane.service.utils.URLEncodeDecode;
import au.com.twobit.yosane.service.utils.UUIDTicketGenerator;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
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
        configureSend();
        if ( configuration.isMockScannerModule() ) {
            MockSaneDependencyModule m = new MockSaneDependencyModule();
            requestInjection(m);
            install( m );
        } else {
            install( new SaneDependencyModule() );
        }
    }

    private void configureYosaneSettings() {
        bind(YosaneServiceConfiguration.class).toInstance(configuration);
        // set up temporary holding area location
        bind(String.class).annotatedWith(Names.named("holdingArea")).toInstance("/tmp/yosane/");
        // set up output image format
        bind(String.class).annotatedWith(Names.named("imageOutputFormat")).toInstance(ImageFormat.png.name());
        // register the file storage class for image persistence
        bind(Storage.class).to(FileStorage.class);
        bind(String.class).annotatedWith(Names.named("staleTime")).toInstance( configuration.getFileStorageConfiguration().getStaleTime() );
        bind(ArtifactCleanup.class).to(FileStorageArtifactCleanup.class);
        // set the default thumbnail scale length (width)
        requestStaticInjection(CreateThumbnail.class);
        bind(Integer.class).annotatedWith(Names.named("scaleWidth")).toInstance(180);        
    }

    private void configureMiscellany() {
        // register a ticket generator
        bind(TicketGenerator.class).to(UUIDTicketGenerator.class);
        bind(EncodeDecode.class).to(URLEncodeDecode.class);
        bind(EventBus.class).toInstance( new EventBus() );
    }

    private void configureHalBuilder() {
        // add a HAL representation factory to be re-used through resources
        DefaultRepresentationFactory jsonFactory = new JsonRepresentationFactory();
        jsonFactory.withFlag( DefaultRepresentationFactory.PRETTY_PRINT );
        jsonFactory.withFlag( DefaultRepresentationFactory.SINGLE_ELEM_ARRAYS);
        bind(DefaultRepresentationFactory.class).toInstance( jsonFactory );        
   }

   private void configureSend() {
       bind(EmailConfiguration.class).toInstance( configuration.getEmailConfiguration());
       bind(LocalDirectoryConfiguration.class).toInstance( configuration.getLocalDirectoryConfiguration() );
       bind(SendFiles.class).annotatedWith( Names.named("sendEmail")).to(SendFilesEmailACE.class);
       bind(SendFiles.class).annotatedWith( Names.named("sendLocalFile")).to( SendFilesLocalDir.class);
       install( new FactoryModuleBuilder()
                   .implement(ContentDelivery.class, ContentDeliveryImpl.class)
                   .build(ContentDeliveryFactory.class));
   }
   
   @Provides
   public CreatePDFDocument createCreatePDFDocument(@Named("imageOutputFormat") String imageOutputFormat) {
       return new CreatePDFDocument(imageOutputFormat);
   }
   
   @Provides
   public PdfArtifactCreator createPdfArtifactCreator(CreatePDFDocument createPdfDocument) {
       return new PdfArtifactCreator(createPdfDocument);
   }
}
