package au.com.twobit.yosane.api;

import java.util.Collection;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentOfImages {
    private String id;
    @NotNull private String name;
    private Collection<Image> images;
    
    public DocumentOfImages() {
        
    }
    
    public DocumentOfImages(String id, String name, Collection<Image> imageList) {
        this.id = id;
        this.name = name;
        this.images = imageList;
    }
    
    @JsonProperty
    public String getId() {
        return id;
    }
    
    @JsonProperty
    public String getName() {
        return name;
    }
    
    @JsonProperty
    public Collection<Image> getImages() {
        return images;
    }
    
    
}
