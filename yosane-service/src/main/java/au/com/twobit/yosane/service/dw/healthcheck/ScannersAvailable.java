package au.com.twobit.yosane.service.dw.healthcheck;

import javax.inject.Inject;

import au.com.twobit.yosane.service.device.ScanHardware;

import com.codahale.metrics.health.HealthCheck;

public class ScannersAvailable extends HealthCheck {

    private ScanHardware hardware = null;

    public ScannersAvailable() {
        super();
    }

    @Inject
    public ScannersAvailable(ScanHardware hardware) {
        super();
        this.hardware = hardware;
    }

    @Override
    protected Result check() throws Exception {
        try {
            if (hardware.getListOfScanDevices().size() > 0) {
                return Result.healthy();
            }
            throw new Exception("There are no scanning devices available");
        } catch (Exception x) {
            return Result.unhealthy(x.getMessage());
        }
    }

}
