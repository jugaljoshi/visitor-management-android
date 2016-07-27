package visitor.app.com.visitormanagement.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import visitor.app.com.visitormanagement.utils.Constants;
import visitor.app.com.visitormanagement.utils.UIUtil;

/**
 * Created by jugal on 23/7/16.
 */
public class SplashActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        launchLoginOrHomeActivity(preferences.getString(Constants.AUTH_TOKEN, null));
    }

    private void launchLoginOrHomeActivity(String authToken){
        if(UIUtil.isEmpty(authToken)){
            Intent loginLoginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginLoginIntent);
            finish();
        }else {
            Intent homeActivityIntent = new Intent(this, WorkBookHomeActivity.class);
            startActivity(homeActivityIntent);
            finish();
        }
    }

    @Override
    public String getScreenTag() {
        return getClass().getSimpleName();
    }
}
