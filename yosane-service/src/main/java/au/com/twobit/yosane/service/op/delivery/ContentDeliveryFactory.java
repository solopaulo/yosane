package au.com.twobit.yosane.service.op.delivery;

import java.util.Map;

import au.com.twobit.yosane.service.send.SendFiles;


public interface ContentDeliveryFactory {
    
    public ContentDelivery create(String [] imageIdentifiers, Map<String,String> deliverySettings, SendFiles sendFiles, ArtifactCreator artifactCreator );
}
