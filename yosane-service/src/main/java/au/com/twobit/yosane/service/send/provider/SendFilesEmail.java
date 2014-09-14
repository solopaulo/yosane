package au.com.twobit.yosane.service.send.provider;

import au.com.twobit.yosane.service.send.SendFiles;

public abstract interface SendFilesEmail extends SendFiles {
    public static final String DEFAULT_SUBJECT = "Here is your image from Yosane, the email";
    public static final String RECIPIENT = "recipient";
}
