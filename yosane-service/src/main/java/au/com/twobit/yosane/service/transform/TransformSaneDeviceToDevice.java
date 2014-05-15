package au.com.twobit.yosane.service.transform;

import au.com.southsky.jfreesane.SaneDevice;
import au.com.twobit.yosane.api.Device;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import com.google.common.base.Function;

public class TransformSaneDeviceToDevice implements Function<SaneDevice, Device> {
	@Override
	public Device apply(SaneDevice device) {
		Device d = new Device();	
		d.setVendor( device.getVendor() );
		d.setModel( device.getModel());
		d.setType( device.getType() );
		d.setName( device.getName() );
		d.setId( Base64Coder.encodeString( device.getName()));
		return d;
	}

}
