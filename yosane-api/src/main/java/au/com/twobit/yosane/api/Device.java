package au.com.twobit.yosane.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Device {
	
	private final String vendor;
	private final String model;
	private final String type;	
	private final String name;
	private final String id;
	
	public Device(String vendor, String model, String type, String name, String id) {
	    this.vendor = vendor;
	    this.model = model;
	    this.type = type;
	    this.name = name;
	    this.id = id;
	}
	
	@JsonProperty
	public String getVendor() {
		return vendor;
	}
	

	@JsonProperty
	public String getModel() {
		return model;
	}
	

	@JsonProperty
	public String getType() {
		return type;
	}
	

	@JsonProperty
	public String getName() {
		return name;
	}
	

	@JsonProperty
	public String getId() {
		return id;
	}
	
}
