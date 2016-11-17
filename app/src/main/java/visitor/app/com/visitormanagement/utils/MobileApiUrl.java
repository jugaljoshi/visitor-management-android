package visitor.app.com.visitormanagement.utils;

/**
 * Created by jugal on 16/7/16.
 */
public final class MobileApiUrl {
    public static final String API_PATH = "/mapi/v1.0.0/";
    public static final String DOMAIN_NAME = "http://192.168.1.10:8080";

    private MobileApiUrl() {
    }

    public static String getMobileApiUrl() {
        return DOMAIN_NAME + API_PATH;
    }
}
