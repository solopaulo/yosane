package au.com.twobit.yosane.service.dw.config;

import java.util.List;

import com.google.common.collect.Lists;

public class LocalDirectoryConfiguration {
    private boolean createDirectoryForEachDay = true;
    private List<String> localPaths = Lists.newArrayList();
    private String defaultDirectory;
    
    public boolean isCreateDirectoryForEachDay() {
        return createDirectoryForEachDay;
    }
    
    public void setCreateDirectoryForEachDay(boolean createDirectoryForEachDay) {
        this.createDirectoryForEachDay = createDirectoryForEachDay;
    }
    
    public List<String> getLocalPaths() {
        return localPaths;
    }
    
    public void setLocalPaths(List<String> localPaths) {
        this.localPaths = localPaths;
    }

    public String getDefaultDirectory() {
        return defaultDirectory;
    }

    public void setDefaultDirectory(String defaultDirectory) {
        this.defaultDirectory = defaultDirectory;
    }
    
}
