package au.com.twobit.yosane.service.dw.task;

import io.dropwizard.servlets.tasks.Task;

import java.io.PrintWriter;

import au.com.twobit.yosane.service.storage.Storage;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Inject;

public class ImageCleanupTask extends Task {
    final private Storage storage;
    
    @Inject
    public ImageCleanupTask(Storage storage) {
        super("cleanup");
        this.storage = storage;
    }
    
    @Override
    public void execute(ImmutableMultimap<String, String> arg0, PrintWriter arg1) throws Exception {
        storage.cleanup();
        arg1.write("Done!");
    }

}
