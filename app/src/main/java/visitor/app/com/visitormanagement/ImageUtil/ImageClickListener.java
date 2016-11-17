package visitor.app.com.visitormanagement.ImageUtil;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import visitor.app.com.visitormanagement.activities.BaseActivity;
import visitor.app.com.visitormanagement.interfaces.AppOperationAware;

/**
 * Created by jugal on 26/10/16.
 */

/**
 * Created by manu on 3/6/16.
 */

public class ImageClickListener implements View.OnClickListener {
    private AppCompatActivity appCompatActivity;

    public ImageClickListener(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    @Override
    public void onClick(View v) {
        if (appCompatActivity == null) return;
        String imageUrl = (String) v.getTag();
        if (TextUtils.isEmpty(imageUrl) || appCompatActivity == null) return;

        showImageDialog(imageUrl, appCompatActivity);
    }

    private void showImageDialog(String imageUrl, AppCompatActivity appCompatActivity) {
        FragmentTransaction ft = appCompatActivity.getSupportFragmentManager().beginTransaction();
        Fragment f = appCompatActivity.getSupportFragmentManager().findFragmentByTag("image_dialog_flag");
        if (f == null) {
            ShowImageDialog showImageDialog = ShowImageDialog.newInstance(imageUrl);
            try {
                if (showImageDialog != null) {
                    showImageDialog.show(ft, "image_dialog_flag");
                }
            } catch (Exception e) {
            }
        }
    }

}
