package visitor.app.com.visitormanagement.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;

public class BaseApplication extends Application {

    private static volatile Context sContext;
    public BaseApplication() {
        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context appContext = getApplicationContext();
        sContext = appContext;
        try {
            Picasso p = new Picasso.Builder(appContext)
                    .memoryCache(new LruCache(getMemCacheSize()))
                    .build();
            Picasso.setSingletonInstance(p);
        } catch (Throwable ex) {
            //Crashlytics.logException(ex);
        }
    }

    private int getMemCacheSize() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        boolean largeHeap = (getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        int memoryClass = am.getMemoryClass();
        if (largeHeap) {
            memoryClass = am.getLargeMemoryClass();
        }
        // Target ~10% of the available heap.
        return 1024 * 1024 * memoryClass / 10;
    }

}
