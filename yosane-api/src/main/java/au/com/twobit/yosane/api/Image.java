package au.com.twobit.yosane.api;

public class Image {
	private String identifier;
	private String outputFormat;
	private ImageStatus status;
	
	public ImageStatus getStatus() {
		return status;
	}
	public void setStatus(ImageStatus status) {
		this.status = status;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getOutputFormat() {
		return outputFormat;
	}
	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}
}
