package au.com.twobit.yosane.api;

public class ErrorCode {
    private final String code;
    private final String description;
    
    public ErrorCode (String code,String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
