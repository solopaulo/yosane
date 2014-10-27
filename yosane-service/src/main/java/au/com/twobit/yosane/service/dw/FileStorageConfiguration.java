package au.com.twobit.yosane.service.dw;

public class FileStorageConfiguration {
    private String staleTime = "1d";

    public String getStaleTime() {
        return staleTime;
    }

    public void setStaleTime(String staleTime) {
        this.staleTime = staleTime;
    }
}
