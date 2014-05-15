package au.com.twobit.yosane.service.di;

import io.dropwizard.lifecycle.Managed;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import au.com.twobit.yosane.service.image.ImageFormat;
import au.com.twobit.yosane.service.storage.FileStorage;
import au.com.twobit.yosane.service.storage.Storage;
import au.com.twobit.yosane.service.utils.TicketGenerator;
import au.com.twobit.yosane.service.utils.UUIDTicketGenerator;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class YosaneGuiceModule extends AbstractModule {

    public YosaneGuiceModule() {
    }

    @Override
    protected void configure() {
        // register a ticket generator
        bind(TicketGenerator.class).to(UUIDTicketGenerator.class);

        // start a new executor service for background tasks
        int maxThreads = 3;
        final ExecutorService executorService = Executors.newFixedThreadPool(maxThreads);
        bind(ExecutorService.class).toInstance(executorService);
        bind(Managed.class).annotatedWith(Names.named("async")).toInstance(new Managed() {
            @Override
            public void start() throws Exception {
            }

            @Override
            public void stop() throws Exception {
                executorService.shutdown();
            }
        });

        // configure the sane dependencies
        install(new SaneDependencyModule());
        // set up some constants
        bind(String.class).annotatedWith(Names.named("holdingArea")).toInstance("/tmp/yosane/");
        bind(String.class).annotatedWith(Names.named("imageOutputFormat")).toInstance(ImageFormat.png.name());
        // register the file storage class for image persistence
        bind(Storage.class).to(FileStorage.class);
    }

}
