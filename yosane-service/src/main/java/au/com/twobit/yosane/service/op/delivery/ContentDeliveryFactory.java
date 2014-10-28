package au.com.twobit.yosane.service.op.delivery;

import java.util.Map;


public interface ContentDeliveryFactory {
    
    public ContentDelivery create(String [] imageIdentifiers, Map<String,String> deliverySettings, ArtifactCreator artifactCreator );
}
