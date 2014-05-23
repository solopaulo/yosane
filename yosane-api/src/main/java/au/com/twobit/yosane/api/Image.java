package au.com.twobit.yosane.api;

public class Image {
	final private String identifier;
	final private String outputFormat;
	final private ImageStatus status;
	
	public Image(String identifier, String outputFormat, ImageStatus status) {
	    this.identifier = identifier;
	    this.outputFormat = outputFormat;
	    this.status = status;
	}
	
	public ImageStatus getStatus() {
		return status;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String getOutputFormat() {
		return outputFormat;
	}
}
