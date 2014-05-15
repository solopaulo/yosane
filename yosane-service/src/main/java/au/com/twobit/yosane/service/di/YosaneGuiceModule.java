package au.com.twobit.yosane.service.di;


import au.com.twobit.yosane.service.image.ImageFormat;
import au.com.twobit.yosane.service.storage.FileStorage;
import au.com.twobit.yosane.service.storage.Storage;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class YosaneGuiceModule extends AbstractModule {

	public YosaneGuiceModule() {		
	}

	@Override
	protected void configure() {
		// configure the sane dependencies
		install(new SaneDependencyModule());
		// set up some constants
		bind(String.class).annotatedWith(Names.named("holdingArea")).toInstance("/tmp/yosane/");
		bind(String.class).annotatedWith(Names.named("imageOutputFormat")).toInstance(ImageFormat.png.name());
		// register the file storage class for image persistence 
		bind(Storage.class).to(FileStorage.class);
	}

}
