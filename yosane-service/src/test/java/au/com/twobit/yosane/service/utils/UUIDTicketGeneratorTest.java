package au.com.twobit.yosane.service.utils;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UUIDTicketGeneratorTest {

    TicketGenerator tgen;
    
    @Before
    public void onSetup() {
        tgen = new UUIDTicketGenerator();
    }
    
    @Test
    public void testTicketGenerationDoesNotReturnNull() {
        Assert.assertNotNull(tgen.newTicket());
    }
    
    @Test
    public void testLargeSetHasNoDuplicates() {
        Set<String> noDupes = new HashSet<String>();
        final int count = 100000;
        for (int i = 0; i < count; i++) {
            noDupes.add( tgen.newTicket());
        }
        Assert.assertEquals(count, noDupes.size());
    }

}
