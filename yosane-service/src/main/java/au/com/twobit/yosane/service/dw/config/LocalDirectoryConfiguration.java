package au.com.twobit.yosane.service.dw.config;

import java.util.Map;

import com.google.common.collect.Maps;

public class LocalDirectoryConfiguration {
    private boolean createDirectoryForEachDay = true;
    private Map<String,String> localPaths = Maps.newHashMap();
    private String defaultDirectory;
    
    public boolean isCreateDirectoryForEachDay() {
        return createDirectoryForEachDay;
    }
    
    public void setCreateDirectoryForEachDay(boolean createDirectoryForEachDay) {
        this.createDirectoryForEachDay = createDirectoryForEachDay;
    }
    
    public Map<String,String> getLocalPaths() {
        return localPaths;
    }
    
    public void setLocalPaths(Map<String,String> localPaths) {
        this.localPaths = localPaths;
    }

    public String getDefaultDirectory() {
        return defaultDirectory;
    }

    public void setDefaultDirectory(String defaultDirectory) {
        this.defaultDirectory = defaultDirectory;
    }
    
}
