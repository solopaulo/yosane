package au.com.twobit.yosane.service.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;

public class URLEncodeDecode implements EncodeDecode {
    private final static String charset = "UTF-8";
    @Override
    public String encodeString(String encodeable) {
        if ( encodeable == null ) {
            return null;
        }
        try {
             return URLEncoder.encode(encodeable, charset);
        } catch (Exception x) {
            // not expected
        }
        return encodeable;
    }

    @Override
    public String decodeString(String decodeable) {
        if ( decodeable == null ) {
            return null;
        }
        try {
            return URLDecoder.decode(decodeable, charset);
        } catch (Exception x) {
            // not expected
        }
        return decodeable;
    }

}
