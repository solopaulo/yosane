package au.com.twobit.yosane.service.transform;

import java.util.List;

import au.com.southsky.jfreesane.OptionValueType;
import au.com.southsky.jfreesane.SaneOption;
import au.com.twobit.yosane.api.DeviceOption;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class TransformSaneOptionToDeviceOption implements Function<SaneOption, DeviceOption> {

	@Override
	public DeviceOption apply(SaneOption option) {
		DeviceOption dopt = new DeviceOption();
		dopt.setName(option.getName());
		dopt.setDescription(option.getDescription());
		dopt.setType(option.getType().name());
		dopt.setTitle(option.getTitle());
		if (OptionValueType.STRING.equals(option.getType())) {
			try {
				dopt.setValue(option.getStringValue());
			} catch (Exception x) {
				x.printStackTrace();
			}

			try {
				dopt.setValues(option.getStringConstraints());
			} catch (Exception x) {
				x.printStackTrace();
			}
		}
		return dopt;
	}

}
