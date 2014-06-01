package au.com.twobit.yosane.service.dw;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

import org.joda.time.Period;

import au.com.twobit.yosane.service.dw.healthcheck.ScannersAvailable;
import au.com.twobit.yosane.service.guice.YosaneGuiceModule;
import au.com.twobit.yosane.service.resource.DocumentsResource;
import au.com.twobit.yosane.service.resource.HomeResource;
import au.com.twobit.yosane.service.resource.ImagesResource;
import au.com.twobit.yosane.service.resource.ScannersResource;
import au.com.twobit.yosane.service.storage.ArtifactCleanup;

import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class YosaneService extends Application<YosaneServiceConfiguration> {

    private Injector injector;
    
    public YosaneService() {
    }
        
    @Override
    public void initialize(Bootstrap<YosaneServiceConfiguration> configuration) {
        configuration.addBundle( new AssetsBundle("/assets","/assets"));
    }

    @Override
    public void run(YosaneServiceConfiguration configuration, Environment env) throws Exception {
        // setup an executor service
        ExecutorService executorService = env.lifecycle().executorService("async").maxThreads(3).build();
        YosaneGuiceModule module = new YosaneGuiceModule(executorService);
        
        injector = Guice.createInjector(module);
        // add resource for home
        env.jersey().register(injector.getInstance(HomeResource.class));
        // add resource for scanner
        env.jersey().register(injector.getInstance(ScannersResource.class));
        // add resource for image
        env.jersey().register(injector.getInstance(ImagesResource.class));
        // add resource for document
        env.jersey().register(injector.getInstance(DocumentsResource.class));
        // add health check
        env.healthChecks().register("Scanner Availability", injector.getInstance(ScannersAvailable.class));
        // add a timer to run the artifact cleanup
        new Timer().schedule( new TimerTask() {
                @Override
                public void run() {
                    injector.getInstance(ArtifactCleanup.class).run();
                }
            }, 
            Period.seconds(45).toStandardDuration().getMillis(),
            Period.days(1).toStandardDuration().getMillis()
         );
    }

    public static void main(String[] args) {
        try {
            File banner = new File(new URI(YosaneService.class.getResource("/banner.txt").toString()));
            System.out.println(Files.toString(banner, Charset.defaultCharset()));
            new YosaneService().run(args);
        } catch (Exception x) {
            System.err.println(String.format("Abnormal exit: %s", x.getMessage()));
        }
    }

}
