package au.com.twobit.yosane.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Device {
	
	private String vendor;
	private String model;
	private String type;	
	private String name;
	private String id;
	
	@JsonProperty
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	@JsonProperty
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	@JsonProperty
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@JsonProperty
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonProperty
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
