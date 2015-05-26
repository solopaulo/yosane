package au.com.twobit.yosane.service.send.provider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.twobit.yosane.service.dw.config.LocalDirectoryConfiguration;
import au.com.twobit.yosane.service.storage.Storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

@RunWith(MockitoJUnitRunner.class )
public class SendFilesLocalDirTest {
    private SendFilesLocalDir local = null;
    
    @Mock
    private LocalDirectoryConfiguration configuration;
    @Mock
    private Storage storage;
    
    @Before
    public void setUp() {
        local = Mockito.spy( new SendFilesLocalDir(configuration) );        
    }

    @Test
    public void testSendFilesToFilesAreNull() {
        try {
            local.sendFilesTo(Maps.<String,String>newHashMap(), (File[])null);
            Assert.fail();
        } catch (Exception x) {
            Assert.assertEquals("No files presented for sending to local directory",x.getMessage());
        }
    }
    
    @Test
    public void testSendFilesToFilesAreEmpty() {
        try {
            local.sendFilesTo(Maps.<String,String>newHashMap(), new File[] { });
            Assert.fail();
        } catch (Exception x) {
            Assert.assertEquals("No files presented for sending to local directory",x.getMessage());
        }
    }
    
    @Test
    public void testSendFilesToNullLocalDirNotValidLocalPath() {
        try {
            local.sendFilesTo(Maps.<String,String>newHashMap(), new File[] { new File("/tmp") });
            Assert.fail();
        } catch (Exception x) {
            Assert.assertEquals("Not an acceptable output path: null",x.getMessage());
        }
    }
    
    @Test
    public void testSendFilesToNullLocalUnacceptableDefault() {
        try {
            Map<String,String> settings = Maps.newHashMap();
            Mockito.when( configuration.getDefaultDirectory()).thenReturn("/toot");
            Mockito.when( configuration.getLocalPaths() ).thenReturn( Lists.newArrayList("/root","/boot"));
            local.sendFilesTo(settings, new File[] { new File("/tmp") });
            Assert.fail();
        } catch (Exception x) {
            Assert.assertEquals("Not an acceptable output path: /toot",x.getMessage());
        }        
    }
    
    @Test
    public void testSendFilesToDailyDirCreatedWhenFlagSet() throws Exception {
        Mockito.when( configuration.isCreateDirectoryForEachDay() ).thenReturn(true);
        
        File tempDir = Files.createTempDir();
        File tempFile = File.createTempFile("abc", "123");
        Mockito.when ( configuration.getLocalPaths() ).thenReturn( Lists.newArrayList( tempDir.getPath()));
        try {
            Mockito.when(configuration.getDefaultDirectory()).thenReturn(tempDir.getPath());
            local.sendFilesTo(Maps.<String,String>newHashMap(), new File[] { tempFile });
            for ( File child : tempDir.listFiles() ) {
                if ( child.listFiles().length > 0 ) {
                    Assert.assertTrue( child.listFiles()[0].getName().equals(tempFile.getName()));
                    child.listFiles()[0].delete();
                }
                Assert.assertTrue( child.delete() );
            }
        } catch (Exception x) {
            x.printStackTrace();
            Assert.fail(x.getMessage());
        } finally {
            Assert.assertTrue( tempDir.delete() );
            tempFile.delete();
        }
        Mockito.verify(local, Mockito.times(1)).createDirectory( Mockito.any( File.class ) , Mockito.anyString());
    }

    @Test
    public void testSendFilesToDailyDirNOTCreatedWhenFlagSetFalse() throws Exception {
        Mockito.when( configuration.isCreateDirectoryForEachDay() ).thenReturn(false);
        
        File tempDir = Files.createTempDir();
        File tempFile = File.createTempFile("abc", "123");
        Mockito.when ( configuration.getLocalPaths() ).thenReturn( Lists.newArrayList( tempDir.getPath()));
        try {
            Mockito.when(configuration.getDefaultDirectory()).thenReturn(tempDir.getPath());
            local.sendFilesTo(Maps.<String,String>newHashMap(), new File[] { tempFile });
            int count = tempDir.listFiles().length;
            Assert.assertEquals(1, count);
            File copiedTempFile = tempDir.listFiles()[0];
            Assert.assertEquals(tempFile.getName(),copiedTempFile.getName());
            Assert.assertTrue( copiedTempFile.delete());
        } catch (Exception x) {
            x.printStackTrace();
            Assert.fail(x.getMessage());
        } finally {
            Assert.assertTrue( tempDir.delete() );
            tempFile.delete();
        }
        Mockito.verify(local, Mockito.times(0)).createDirectory( Mockito.any( File.class ), Mockito.anyString());
    }

    @Test
    public void testValidateLocalPathNoPathSpecified() {
        try {
            local.validateLocalPath(null);
            Assert.fail();
        } catch (Exception x) {
            Assert.assertEquals("No path specified",x.getMessage());
        }
    }
    
    @Test
    public void testValidateLocalPathExistCannotBeWritten() {
        try {
            local.validateLocalPath( new File("/root"));
            Assert.fail();
        } catch (Exception x) {
            Assert.assertEquals("Cannot write", x.getMessage());
        }
    }
    
    @Test
    public void testValidateLocalPathNotExistCannotBeCreated() {
        File tempDir = null;
        try {
            tempDir = Mockito.spy( Files.createTempDir() );
            Mockito.when( tempDir.exists() ).thenReturn(false);
            Mockito.when( tempDir.mkdir() ).thenReturn(false);
            local.validateLocalPath( tempDir );
            Assert.fail();
        } catch (Exception x) {
            Assert.assertEquals("Cannot create", x.getMessage());
        } finally {
            Assert.assertTrue( tempDir.delete() );            
        }
    }

    @Test
    public void testCreateDailyDirectory() {
        // create a temp directory for the test
        File tempDir = Files.createTempDir();
        Assert.assertTrue(tempDir.exists() && tempDir.canWrite());
        
        // run create daily with this dir
        File dailyDir = null; 
        try {
            dailyDir = local.createDailyDirectory(tempDir);
        } catch (Exception x) {
            tempDir.delete();
            x.printStackTrace();
            Assert.fail(x.getMessage());
        }
        
        // verify dir exists and writable
        Assert.assertEquals(dailyDir.getName(), new SimpleDateFormat("YYYY-MM-dd").format( DateTime.now().toDate() ));
        Assert.assertTrue(dailyDir.exists() && dailyDir.canWrite());
        // rm daily dir
        Assert.assertTrue( dailyDir.delete() );
        
        // rm temp directory
        Assert.assertTrue( tempDir.delete() );
    }
    
    
    @Test
    public void testIsAcceptablePath() {
        List<String>acceptablePaths = Lists.newArrayList(
                "/tmp/acceptable",
                "/tmp/acceptedWithoutTrailingSlash/"
                );
        Assert.assertTrue( local.isAcceptablePath("/tmp/acceptable", acceptablePaths) );
        Assert.assertTrue( local.isAcceptablePath("/tmp/acceptable/", acceptablePaths) );
        Assert.assertTrue( local.isAcceptablePath("/tmp/acceptedWithoutTrailingSlash", acceptablePaths) );
        Assert.assertTrue( local.isAcceptablePath("/tmp/acceptedWithoutTrailingSlash/", acceptablePaths) );
        Assert.assertFalse( local.isAcceptablePath("/tmp/", acceptablePaths));
        Assert.assertFalse( local.isAcceptablePath("/tmp/acceptedWithoutTrailingSlash/ButThisNot", acceptablePaths) );
    }
    
    @Test
    public void testFormatterIsSane() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get( Calendar.YEAR );
        int m = cal.get( Calendar.MONTH ) + 1;
        int d = cal.get( Calendar.DAY_OF_MONTH );
                
        String calendarDate = String.format("%s-%02d-%02d",y,m,d);
        Assert.assertEquals(calendarDate,SendFilesLocalDir.sdf.format( new Date() ));
    }
}
