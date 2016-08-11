package visitor.app.com.visitormanagement.activities;

/**
 * Created by jugal on 16/7/16.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.fragment.AbstractFragment;
import visitor.app.com.visitormanagement.fragment.ConfirmationDialogFragment;
import visitor.app.com.visitormanagement.fragment.ProgressDialogFragment;
import visitor.app.com.visitormanagement.interfaces.ApiErrorAware;
import visitor.app.com.visitormanagement.interfaces.AppOperationAware;
import visitor.app.com.visitormanagement.interfaces.FragmentCodes;
import visitor.app.com.visitormanagement.interfaces.NavigationCodes;
import visitor.app.com.visitormanagement.utils.Constants;
import visitor.app.com.visitormanagement.utils.DataUtil;
import visitor.app.com.visitormanagement.utils.DialogButton;
import visitor.app.com.visitormanagement.utils.MessageHandler;
import visitor.app.com.visitormanagement.utils.UIUtil;

public abstract class BaseActivity extends AppCompatActivity implements
        AppOperationAware, ApiErrorAware,
        ConfirmationDialogFragment.ConfirmationDialogCallback {

    protected MessageHandler handler;
    private boolean isActivitySuspended;
    private ProgressDialog progressDialog = null;

    private String PROGRESS_DIALOG_TAG;
    private LayoutInflater layoutInflater;

    public static void showKeyboard(final View view) {
        (new Handler()).postDelayed(new Runnable() {

            public void run() {
                MotionEvent motionActionDown = MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0);
                MotionEvent motionActionUp = MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0);
                if (motionActionDown == null || motionActionUp == null || view == null) return;
                view.dispatchTouchEvent(motionActionDown);
                view.dispatchTouchEvent(motionActionUp);
                if (view instanceof EditText) {
                    EditText editText = (EditText) view;
                    editText.setSelection(editText.getText().length());
                }
            }
        }, 100);
    }

    public static void hideKeyboard(Context context, View view) {
        if (context == null || view == null) return;
        IBinder token = view.getWindowToken();
        if (token == null) return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token, 0);
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected static void startPermissionsSettingsActivity(Activity activity) {
        try {
            final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            activity.startActivityForResult(intent, NavigationCodes.RC_PERMISSIONS_SETTINGS);
        } catch (ActivityNotFoundException ex) {
            //Crashlytics.logException(ex);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutInflater = getLayoutInflater().cloneInContext(this);
        handler = new MessageHandler<>(this);
        isActivitySuspended = false;
    }

    @NonNull
    @Override
    public LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (name.equals(LAYOUT_INFLATER_SERVICE)) {
            if (layoutInflater == null) {
                layoutInflater = (LayoutInflater) super.getSystemService(name);
            }
            return layoutInflater;
        }
        return super.getSystemService(name);
    }

    @Override
    public void onBackPressed() {
        //Workaround to avoid IllegalStateException: Can not performAnimation this action after onSaveInstanceState
        onStateNotSaved();
        try {
            super.onBackPressed();
        } catch (Exception ex) {
            //Crashlytics.logException(ex);
        }
    }

    @Override
    public boolean checkInternetConnection() {
        return DataUtil.isInternetAvailable(getApplicationContext());
    }

    @Override
    public void showProgressDialog(String msg) {
        showProgressDialog(msg, true);
    }

    @Override
    public void showProgressDialog(String msg, boolean cancelable) {
        showProgressDialog(msg, cancelable, false);
    }

    @Override
    public void showProgressDialog(String msg, boolean cancelable, boolean isDeterminate) {
        if (isSuspended()) return;
        String progressDialogTag = getProgressDialogTag();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(progressDialogTag);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (fragment != null) {
            ft.remove(fragment);
        }
        fragment = ProgressDialogFragment.newInstance(msg, cancelable, isDeterminate);
        ft.add(fragment, progressDialogTag);
        if (!isSuspended()) {
            try {
                ft.commitAllowingStateLoss();
            } catch (IllegalStateException ex) {
                //Crashlytics.logException(ex);
            }
        }
    }

    @Override
    public void hideProgressDialog() {
        String progressDialogTag = getProgressDialogTag();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(progressDialogTag);
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            try {
                ft.remove(fragment);
            } finally {
                if (!isSuspended()) {
                    ft.commitAllowingStateLoss();
                }
            }
        }
    }

    private String getProgressDialogTag() {
        if (PROGRESS_DIALOG_TAG == null) {
            synchronized (this) {
                if (PROGRESS_DIALOG_TAG == null) {
                    PROGRESS_DIALOG_TAG = getScreenTag() + "#ProgressDilog";
                }
            }
        }
        return PROGRESS_DIALOG_TAG;
    }

    @Override
    public void showProgressView() {
        showProgressDialog(getString(R.string.please_wait));
    }

    public void showProgressView(boolean cancellable) {
        showProgressDialog(getString(R.string.please_wait), cancellable);
    }

    @Override
    public void hideProgressView() {
        if (isSuspended()) return;
        try {
            hideProgressDialog();
        } catch (IllegalArgumentException e) {

        }
    }

    @Override
    public BaseActivity getCurrentActivity() {
        return this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivitySuspended = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivitySuspended = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivitySuspended = true;
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isActivitySuspended = false;
    }

    protected void onResume() {
        super.onResume();
        isActivitySuspended = false;
    }

    public void showAlertDialog(CharSequence title, CharSequence msg) {
        showAlertDialog(title, msg, -1);
    }

    public void showAlertDialog(CharSequence title, CharSequence msg, int requestCode) {
        showAlertDialog(title, msg, requestCode, null);
    }

    public void showAlertDialog(CharSequence title, CharSequence msg, final int requestCode, final Bundle valuePassed) {
        if (isSuspended())
            return;
        ConfirmationDialogFragment dialogFragment = ConfirmationDialogFragment.newInstance(
                requestCode, title == null ? getString(R.string.app_name) : title, msg, getString(R.string.ok),
                null, false);
        try {
            dialogFragment.show(getSupportFragmentManager(), getScreenTag() + "#AlertDialog");
        } catch (IllegalStateException ex) {
            //Crashlytics.logException(ex);
        }
    }

    @Override
    public final void onDialogConfirmed(int reqCode, Bundle data, boolean isPositive) {
        if (isPositive) {
            onPositiveButtonClicked(reqCode, data);
            if (data != null && data.getBoolean(Constants.FINISH_ACTIVITY, false)) {
                if (data.containsKey(Constants.ACTIVITY_RESULT_CODE)) {
                    setResult(data.getInt(Constants.ACTIVITY_RESULT_CODE, RESULT_OK));
                }
                finish();
            }
        } else {
            onNegativeButtonClicked(reqCode, data);
        }
    }

    @Override
    public void onDialogCancelled(int reqCode, Bundle data) {

    }

    public void showAlertDialogFinish(CharSequence title, CharSequence msg) {
        showAlertDialogFinish(title, msg, -1);
    }

    public void showAlertDialogFinish(CharSequence title, CharSequence msg, final int resultCode) {
        if (isSuspended())
            return;
        Bundle data = new Bundle(2);
        data.putBoolean(Constants.FINISH_ACTIVITY, true);
        data.putInt(Constants.ACTIVITY_RESULT_CODE, resultCode);
        ConfirmationDialogFragment dialogFragment = ConfirmationDialogFragment.newInstance(
                0, title == null ? getString(R.string.app_name) : title, msg, getString(R.string.ok),
                null, data, false);
        try {
            dialogFragment.show(getSupportFragmentManager(), getScreenTag() + "#AlertDialog");
        } catch (IllegalStateException ex) {
            //Crashlytics.logException(ex);
        }
    }

    public void showAlertDialog(CharSequence msg) {
        showAlertDialog(null, msg);
    }

    public void showAlertDialog(CharSequence title,
                                CharSequence msg, @DialogButton.ButtonType int dialogButton,
                                @DialogButton.ButtonType int nxtDialogButton, final int requestCode) {
        showAlertDialog(title, msg, dialogButton, nxtDialogButton, requestCode, null, null);
    }

    public void showAlertDialog(CharSequence title,
                                CharSequence msg, @DialogButton.ButtonType int dialogButton,
                                @DialogButton.ButtonType int nxtDialogButton, final int requestCode,
                                final Bundle passedValue) {
        showAlertDialog(title, msg, dialogButton, nxtDialogButton, requestCode, passedValue, null);
    }

    public void showAlertDialog(CharSequence title,
                                CharSequence msg, @DialogButton.ButtonType int dialogButton,
                                @DialogButton.ButtonType int nxtDialogButton, final int requestCode,
                                final Bundle passedValue, String positiveBtnText) {
        String negativeButtonText = null;

        if (dialogButton != DialogButton.NONE &&
                (dialogButton == DialogButton.YES || dialogButton == DialogButton.OK)) {
            if (TextUtils.isEmpty(positiveBtnText)) {
                int textId = dialogButton == DialogButton.YES ? R.string.yesTxt : R.string.ok;
                positiveBtnText = getString(textId);
            }
        }
        if (nxtDialogButton != DialogButton.NONE &&
                (nxtDialogButton == DialogButton.NO || nxtDialogButton == DialogButton.CANCEL)) {
            int textId = nxtDialogButton == DialogButton.NO ? R.string.noTxt : R.string.cancel;
            negativeButtonText = getString(textId);
        }
        if (isSuspended())
            return;
        // Defensive check
        if (TextUtils.isEmpty(positiveBtnText) && TextUtils.isEmpty(negativeButtonText)) {
            positiveBtnText = getString(R.string.ok);
        }
        ConfirmationDialogFragment dialogFragment = ConfirmationDialogFragment.newInstance(
                requestCode, title == null ? getString(R.string.app_name) : title, msg, positiveBtnText,
                negativeButtonText, passedValue, false);
        try {
            dialogFragment.show(getSupportFragmentManager(), getScreenTag() + "#AlertDialog");
        } catch (IllegalStateException ex) {
            //Crashlytics.logException(ex);
        }
    }

    public void showAlertDialog(CharSequence title,
                                CharSequence msg, String positiveBtnText,
                                String negativeBtnText, final int requestCode,
                                final Bundle passedValue) {
        showAlertDialog(title, msg, positiveBtnText, negativeBtnText, requestCode, passedValue,
                false);
    }

    public void showAlertDialog(CharSequence title, CharSequence msg, String positiveBtnText,
                                String negativeBtnText, final int requestCode,
                                final Bundle passedValue, boolean isCancellable) {
        if (isSuspended())
            return;
        ConfirmationDialogFragment dialogFragment = ConfirmationDialogFragment.newInstance(
                requestCode, title, msg, positiveBtnText,
                negativeBtnText, passedValue, isCancellable);
        try {
            dialogFragment.show(getSupportFragmentManager(), getScreenTag() + "#AlertDialog");
        } catch (IllegalStateException ex) {
            //Crashlytics.logException(ex);
        }
    }

    public void showAlertDialog(CharSequence title,
                                CharSequence msg, @DialogButton.ButtonType int dialogButton,
                                @DialogButton.ButtonType int nxtDialogButton) {
        showAlertDialog(title, msg, dialogButton, nxtDialogButton, 0);
    }

    protected void onPositiveButtonClicked(int sourceName,
                                           Bundle valuePassed) {
        switch (sourceName) {
            case NavigationCodes.GO_TO_LOGIN:
                goToLogin();
                break;
            case NavigationCodes.RC_PERMISSIONS_SETTINGS:
                startPermissionsSettingsActivity(this);
                break;
        }
    }

    protected void onNegativeButtonClicked(int requestCode, Bundle data) {

    }

    public void triggerActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        isActivitySuspended = false;
        if (resultCode == NavigationCodes.GO_TO_HOME) {
            goToHome();
        } else if (resultCode == NavigationCodes.GO_TO_QC) {
            setResult(NavigationCodes.GO_TO_QC);
            finish();
        } else if (resultCode == NavigationCodes.BASKET_CHANGED) {
            // Initiate Fragment callback (if-any) to sync cart
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showToast(String txt) {
        Toast toast = Toast.makeText(this, txt, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    protected String getValueOrBlank(String val) {
        return !TextUtils.isEmpty(val) ? val : "";
    }

    @Override
    public boolean isSuspended() {
        return isActivitySuspended || isFinishing();
    }

    @Override
    public void setSuspended(boolean state) {
        isActivitySuspended = state;
    }

    @Override
    protected void onDestroy() {
        isActivitySuspended = true;
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public MessageHandler getHandler() {
        return handler;
    }

    //public abstract void onChangeFragment(AbstractFragment newFragment);

    @Override
    public void showApiErrorDialog(@Nullable CharSequence title, CharSequence message) {
        showAlertDialog(title, message);
    }

    @Override
    public void showApiErrorDialog(@Nullable CharSequence title, CharSequence message, boolean finish) {
        if (finish) {
            showAlertDialogFinish(title, message);
        } else {
            showAlertDialog(title, message);
        }
    }

    @Override
    public void showApiErrorDialog(@Nullable CharSequence title, CharSequence message, int requestCode, Bundle valuePassed) {
        showAlertDialog(title, message, requestCode, valuePassed);
    }

    @Override
    public void showApiErrorDialog(@Nullable CharSequence title, CharSequence message, int resultCode) {
        showAlertDialogFinish(title, message, resultCode);
    }

    public abstract String getScreenTag();

    /*
    protected void togglePasswordView(EditText passwordEditText, boolean show) {
        Drawable rightDrawable;
        if (!show) {
            rightDrawable = ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.ic_hide_password);
            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);
        } else {
            rightDrawable = ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.ic_show_password);
            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }

        Map<String, String> eventAttribs = new HashMap<>();
        eventAttribs.put(TrackEventkeys.ENABLED, String.valueOf(show));
        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);
    }
    public void launchLogin(String navigationCtx, boolean shouldGoBackToHomePage) {
        launchLogin(navigationCtx, null, shouldGoBackToHomePage);
    }
    */

    /**************
     * code for Android M Support
     ******************/

//    public boolean handlePermission(String permission, int REQUEST_CODE) {
//        return handlePermission(permission, null, REQUEST_CODE);
//    }
    public boolean handlePermission(String permission, String rationale, int requestCode) {
        if (hasPermissionGranted(permission)) {
            return true;
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
                    && !TextUtils.isEmpty(rationale)) {
                Bundle bundle = new Bundle(2);
                bundle.putString(Constants.KEY_PERMISSION, permission);
                bundle.putInt(Constants.KEY_PERMISSION_RC, requestCode);
                showAlertDialog(getString(R.string.permission_rationale_dialog_title), rationale,
                        getString(R.string.action_settings),
                        getString(R.string.cancel),
                        NavigationCodes.RC_PERMISSIONS_SETTINGS,
                        bundle, true);
            } else {
                requestPermission(permission, requestCode);
            }
        }
        return false;
    }

    public boolean hasPermissionGranted(String permission) {
        int result = ContextCompat.checkSelfPermission(this, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
    }

    public void goToHome() {
        Intent intent = new Intent(this, WorkBookHomeActivity.class);
        //intent.putExtra(Constants.FRAGMENT_CODE, FragmentCodes.START_HOME);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void goToLogin() {
        UIUtil.clearDataFromPreference(this);
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra(Constants.FRAGMENT_CODE, FragmentCodes.START_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}

