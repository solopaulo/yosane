package au.com.twobit.yosane.service.di;

import au.com.twobit.yosane.service.device.ScanHardware;
import au.com.twobit.yosane.service.device.sane.SaneScanHardware;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class SaneDependencyModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(String.class).annotatedWith(Names.named("saneHost")).toInstance("pvr");
		bind(Integer.class).annotatedWith(Names.named("sanePort")).toInstance(SaneScanHardware.DEFAULT_SANE_PORT);
		bind(ScanHardware.class).to(SaneScanHardware.class);				
	}

}
