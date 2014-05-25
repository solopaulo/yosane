package au.com.twobit.yosane.api;

import java.util.List;

import javax.validation.constraints.NotNull;

public class DeviceOption {
	@NotNull  private String name;
	 private String description;
	private String group;
	private String title;
	 private String type;
	@NotNull  private String value;
	 private List<String> range;
	 private String constraintType; 

	public DeviceOption() {
	    
	}
	public DeviceOption(String name, String description, String group, String title, String type, String constraintType, String value, List<String> range) {
        this.title = title;
        this.group = group;
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

	public String getGroup() {
	    return group;
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
