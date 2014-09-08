package au.com.twobit.yosane.service.resource.dto;

import java.util.List;

import au.com.twobit.yosane.api.DeviceOption;

public class ScanMessage {
    List<DeviceOption> deviceOptions;
    
    public void setDeviceOptions(List<DeviceOption> deviceOptions) {
        this.deviceOptions = deviceOptions;
    }
    
    public List<DeviceOption> getDeviceOptions() {
        return deviceOptions;
    }
}
