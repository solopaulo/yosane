package au.com.twobit.yosane.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Image {
	private String identifier;
	private String outputFormat;
	private ImageStatus status;
	private int ordering;
	
	public Image(String identifier, String outputFormat, ImageStatus status) {
	    this.identifier = identifier;
	    this.outputFormat = outputFormat;
	    this.status = status;
	}
	
	public Image() {
	    
	}
	
	@JsonProperty
	public ImageStatus getStatus() {
		return status;
	}
	
	@JsonProperty
	public String getIdentifier() {
		return identifier;
	}
	
	public String getOutputFormat() {
		return outputFormat;
	}
	
	@JsonProperty
	public int getOrdering() {
	    return ordering;
	}
	
	public void setOrdering(int ordering) {
	    this.ordering = ordering;
	}
}
