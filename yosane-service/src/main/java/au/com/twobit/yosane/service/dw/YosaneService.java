package au.com.twobit.yosane.service.dw;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.io.File;
import java.nio.charset.Charset;

import au.com.twobit.yosane.service.di.YosaneGuiceModule;
import au.com.twobit.yosane.service.dw.healthcheck.ScannersAvailable;
import au.com.twobit.yosane.service.resource.ImagesResource;
import au.com.twobit.yosane.service.resource.ScannersResource;

import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class YosaneService extends Application<YosaneServiceConfiguration> {

	public YosaneService() {
	}

	@Override
	public void initialize(Bootstrap<YosaneServiceConfiguration> arg0) {
		// TODO Auto-generated method stub		
	}


	@Override
	public void run(YosaneServiceConfiguration configuration, Environment env) throws Exception {
		// initialise Guice with our custom thingies that we like to inject places
		Injector injector = Guice.createInjector( new YosaneGuiceModule() );
		// add resource for scanner
		env.jersey().register( injector.getInstance(ScannersResource.class) );
		// add resource for image
		env.jersey().register( injector.getInstance(ImagesResource.class) );
		// add resource for document
		
		// add health check
		env.healthChecks().register("Scanner Availability", injector.getInstance(ScannersAvailable.class));
	}
	
	public static void main(String [] args) {
		try {
			for (String line : Files.readLines( new File("banner.txt"), Charset.defaultCharset()) ) {
				System.out.println(line);
			}
			new YosaneService().run(args);
		} catch (Exception x) {
			System.err.println(String.format("Abnormal exit: %s",x.getMessage()));
		}
	}


}
