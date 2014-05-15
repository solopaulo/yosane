package au.com.twobit.yosane.service.device;

import java.awt.image.BufferedImage;
import java.util.List;

import au.com.twobit.yosane.api.Device;
import au.com.twobit.yosane.api.DeviceOption;


/** An interface for interacting with scanner devices
 * 
 * @author paul
 *
 */
public interface ScanHardware {
	/** Gets a list of available scanning devices
	 * 
	 * @return
	 */
	public List<Device> getListOfScanDevices();
	
	/** Gets the details of a scanning device
	 * 
	 * @param identifier
	 * @return
	 */
	public Device getScanDeviceDetails(String scanDeviceIdentifier) throws IllegalArgumentException;
	
	/** Gets the options of a scanning device
	 * 
	 * @param identifier Scanner id
	 */
	public List<DeviceOption> getScanDeviceOptions(String scanDeviceIdentifier) throws IllegalArgumentException;
	
	
	/** Acquires an image by using the scanning device
	 * 
	 * @param scanDeviceIdentifier
	 * @return
	 */
	public BufferedImage acquireImage(String scanDeviceIdentifier,String ticket, DeviceOption ... options) throws IllegalArgumentException,Exception;
}
