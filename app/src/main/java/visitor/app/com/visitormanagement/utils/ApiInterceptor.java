package visitor.app.com.visitormanagement.utils;

import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jugal on 16/7/16.
 */
public class ApiInterceptor implements Interceptor {
    @Nullable
    private String bbAuthToken;
    private String appVersion;

    private String userAgent;

    public ApiInterceptor(String appVersion, @Nullable String bbAuthToken) {
        this.appVersion = appVersion;
        this.bbAuthToken = bbAuthToken;
        StringBuilder userAgentBuilder = new StringBuilder("VE ").append("Android/v").append(appVersion).append("/os ")
                .append(Build.VERSION.RELEASE);
        userAgent = userAgentBuilder.toString();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        StringBuilder requestCookieVal = new StringBuilder();
        if (!TextUtils.isEmpty(bbAuthToken)) {
            if (requestCookieVal.length() > 0) {
                requestCookieVal.append(';');
            }
            requestCookieVal.append("AUTH_TOKEN=\"").append(bbAuthToken).append('"');
        }

        Request.Builder newRequestBuilder = originalRequest.newBuilder()
                .removeHeader("User-Agent");
        newRequestBuilder.addHeader("User-Agent", userAgent);
        if (requestCookieVal.length() > 0) {
            newRequestBuilder.addHeader("Cookie", requestCookieVal.toString());
        }
        return chain.proceed(newRequestBuilder.build());
    }
}

