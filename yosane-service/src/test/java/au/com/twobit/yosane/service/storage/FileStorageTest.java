package au.com.twobit.yosane.service.storage;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.twobit.yosane.api.DocumentOfImages;
import au.com.twobit.yosane.api.Image;
import au.com.twobit.yosane.api.ImageStatus;
import au.com.twobit.yosane.api.comparator.ImageComparator;
import au.com.twobit.yosane.service.image.ImageFormat;

import com.google.common.collect.Lists;

public class FileStorageTest {

    FileStorage storage;
    
    @Before
    public void onSetup() {
        storage = new FileStorage("/tmp/yosane",ImageFormat.png.name());
    }
    
    @Test
    public void testLoadOfDocumentSaveOfDocument() throws Exception {
        List<Image> images = Lists.newArrayList();
        images.add( new Image("i1",ImageFormat.png.name(),ImageStatus.READY,new Date()) {{ setOrdering(10); }});
        images.add( new Image("i2",ImageFormat.png.name(),ImageStatus.READY,new Date()) {{ setOrdering(20); }});
        images.add( new Image("i3",ImageFormat.png.name(),ImageStatus.PROCESSING,new Date()) {{ setOrdering(40); }});
        images.add( new Image("i4",ImageFormat.png.name(),ImageStatus.READY,new Date()) {{ setOrdering(30); }});
        Collections.sort(images, new ImageComparator());
        String id = "abcd1234";
        DocumentOfImages document = new DocumentOfImages(id,"my document name",images);
        
        try {
            storage.saveDocument(document, id);
        } catch (StorageException x) {
            x.printStackTrace();
            Assert.fail();
        }
        
        DocumentOfImages loaded = null;
        try {
            loaded = storage.loadDocument(id);
        } catch (Exception x) {
            x.printStackTrace();
            Assert.fail();
        }
        Assert.assertNotNull(loaded);
    }

}
