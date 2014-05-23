package au.com.twobit.yosane.api;

import java.util.List;

import javax.validation.constraints.NotNull;

public class DeviceOption {
	@NotNull final private String name;
	final private String description;
	final private String title;
	final private String type;
	@NotNull final private String value;
	final private List<String> range;
	final private String constraintType; 

	public DeviceOption(String name, String description, String title, String type, String constraintType, String value, List<String> range) {
        this.title = title;
        this.value = value;
        this.range = range;
        this.type = type;
        this.description = description;
        this.name = name;
        this.constraintType = constraintType;
	}
	
	public String getValue() {
		return value;
	}

	public List<String> getRange() {
	    return range;
	}

	public String getTitle() {
		return title;
	}

	public String getType() {
		return type;
	}
	
	public String getConstraintType() {
	    return constraintType;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}
}
