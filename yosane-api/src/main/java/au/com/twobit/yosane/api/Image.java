package au.com.twobit.yosane.api;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Image {
	private String identifier;
	private String outputFormat;
	private Date lastModified;
	private ImageStatus status;
	private int ordering;
	
	public Image(String identifier, String outputFormat, ImageStatus status, Date lastModified) {
	    this.identifier = identifier;
	    this.outputFormat = outputFormat;
	    this.status = status;
	    this.lastModified = lastModified;
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

    public Date getLastModified() {
        return lastModified;
    }

}
