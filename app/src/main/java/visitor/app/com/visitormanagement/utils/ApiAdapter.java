package visitor.app.com.visitormanagement.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import visitor.app.com.visitormanagement.interfaces.ApiService;

/**
 * Created by jugal on 16/7/16.
 */
public class ApiAdapter {

    private static volatile ApiService apiService;
    private static volatile OkHttpClient okHttpClient;

    private ApiAdapter() {
    }

    public static synchronized ApiService getApiService(Context context) {
        if (apiService == null) {
            apiService = refreshApiService(context);
        }
        return apiService;
    }

    public static synchronized void reset() {
        apiService = null;
        okHttpClient = null;
    }


    private static ApiService refreshApiService(Context context) {
        OkHttpClient okHttpClient = getHttpClient(context);

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(MobileApiUrl.getMobileApiUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return restAdapter.create(ApiService.class);
    }

    public static OkHttpClient getHttpClient(Context context) {
        if (okHttpClient != null) return okHttpClient;
        SharedPreferences prefer = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String authToken = prefer.getString(Constants.AUTH_TOKEN, null);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .addInterceptor(new ApiInterceptor(DataUtil.getAppVersion(context), authToken))
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        okHttpClient = builder.build();
        return okHttpClient;
    }

    private static OkHttpClient getBaseHttpClient() {
        return new OkHttpClient().newBuilder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}
