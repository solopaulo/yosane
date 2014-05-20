package au.com.twobit.yosane.service.utils;

import java.nio.charset.Charset;
import java.util.UUID;

import com.google.common.hash.Hashing;

public class UUIDTicketGenerator implements TicketGenerator {

    @Override
    public String newTicket() {
        return Hashing.md5().hashString(UUID.randomUUID().toString(), Charset.defaultCharset()).toString();
    }

}
