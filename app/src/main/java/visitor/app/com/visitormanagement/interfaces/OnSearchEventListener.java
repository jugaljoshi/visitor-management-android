package visitor.app.com.visitormanagement.interfaces;

import android.support.annotation.NonNull;

/**
 * Created by jugal on 9/8/16.
 */
public interface OnSearchEventListener {
    void onSearchRequested(@NonNull String query, @NonNull String wbId);
}
