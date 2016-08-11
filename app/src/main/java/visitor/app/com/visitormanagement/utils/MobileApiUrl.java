package visitor.app.com.visitormanagement.utils;

/**
 * Created by jugal on 16/7/16.
 */
public final class MobileApiUrl {
    public static final String API_PATH = "/mapi/v1.0.0/";
    public static final String DOMAIN_NAME = "http://192.168.1.100:8080";

    private MobileApiUrl() {
    }

    public static String getMobileApiUrl() {
        return DOMAIN_NAME + API_PATH;
    }
}

/*
Once you eliminate the impossible, whatever remains, no matter how improbable, must be the truth.
Start by doing what's necessary, then do what's possible, and suddenly you are doing the impossible.

 */
