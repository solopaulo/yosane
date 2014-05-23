package au.com.twobit.yosane.service.dw;

import io.dropwizard.Application;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;

import au.com.twobit.yosane.service.dw.healthcheck.ScannersAvailable;
import au.com.twobit.yosane.service.guice.YosaneGuiceModule;
import au.com.twobit.yosane.service.resource.HomeResource;
import au.com.twobit.yosane.service.resource.ImagesResource;
import au.com.twobit.yosane.service.resource.ScannersResource;

import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class YosaneService extends Application<YosaneServiceConfiguration> {

    // initialise Guice with our custom thingies that we like to inject
    final private Injector injector;
    
    public YosaneService() {
        injector = Guice.createInjector(new YosaneGuiceModule());
    }
        
    @Override
    public void initialize(Bootstrap<YosaneServiceConfiguration> configuration) {
        // TODO Auto-generated method stub
    }

    @Override
    public void run(YosaneServiceConfiguration configuration, Environment env) throws Exception {
        // add resource for home
        env.jersey().register(injector.getInstance(HomeResource.class));
        // add resource for scanner
        env.jersey().register(injector.getInstance(ScannersResource.class));
        // add resource for image
        env.jersey().register(injector.getInstance(ImagesResource.class));
        // add resource for document
        
        // add health check
        env.healthChecks().register("Scanner Availability", injector.getInstance(ScannersAvailable.class));

        // add managed class to shutdown executor service
        env.lifecycle().manage(injector.getInstance(Key.get(Managed.class, Names.named("async"))));
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
