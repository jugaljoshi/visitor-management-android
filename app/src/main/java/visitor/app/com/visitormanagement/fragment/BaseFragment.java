package visitor.app.com.visitormanagement.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.activities.BaseActivity;
import visitor.app.com.visitormanagement.interfaces.ApiErrorAware;
import visitor.app.com.visitormanagement.utils.Constants;
import visitor.app.com.visitormanagement.utils.DialogButton;
import visitor.app.com.visitormanagement.utils.MessageHandler;

/**
 * Created by jugal on 30/7/16.
 */
public abstract class BaseFragment extends AbstractFragment implements ApiErrorAware,
        ConfirmationDialogFragment.ConfirmationDialogCallback {

    protected MessageHandler handler;
    private String progressDialogTag;
    private View mLoadingView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        handler = new MessageHandler<>(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getView() != null) {
            getView().setClickable(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        setSuspended(false);
        Activity activity = getActivity();
        if (activity != null && activity instanceof BaseActivity) {
            ((BaseActivity) activity).triggerActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showProgressView() {
        if (getActivity() == null) return;
        ViewGroup view = getContentView();
        if (view == null) return;
        view.removeAllViews();
        if (mLoadingView == null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            mLoadingView = inflater.inflate(R.layout.loading_layout, view, false);
        } else {
            if (mLoadingView.getParent() != null) {
                ((ViewGroup) mLoadingView.getParent()).removeView(mLoadingView);
            }
        }
        mLoadingView.setVisibility(View.VISIBLE);
        view.addView(mLoadingView);
    }

    public void hideProgressView() {
        if (getActivity() == null) return;
        ViewGroup view = getContentView();
        if (view == null) return;
        view.removeAllViews();
    }

    public void showProgressDialog(String msg) {
        showProgressDialog(msg, true);
    }

    @Override
    public void showProgressDialog(String msg, boolean cancelable) {
        showProgressDialog(msg, cancelable, false);
    }

    @Override
    public void showProgressDialog(String msg, boolean cancelable, boolean isDeterminate) {
        if (TextUtils.isEmpty(msg)) {
            msg = getResources().getString(R.string.please_wait);
        }
        String progressDialogTag = getProgressDialogTag();
        Fragment fragment = getFragmentManager().findFragmentByTag(progressDialogTag);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (fragment != null) {
            ft.remove(fragment);
        }
        fragment = ProgressDialogFragment.newInstance(msg, cancelable, isDeterminate);
        ft.add(fragment, progressDialogTag);
        if (!isSuspended()) {
            try {
                ft.commit();
            } catch (IllegalStateException ex) {
                //Crashlytics.logException(ex);
            }
        }
    }

    private String getProgressDialogTag() {
        if (progressDialogTag == null) {
            synchronized (this) {
                if (progressDialogTag == null) {
                    progressDialogTag = getScreenTag() + "#ProgressDilog";
                }
            }
        }
        return progressDialogTag;
    }

    public void hideProgressDialog() {
        String progressDialogTag = getProgressDialogTag();
        Fragment fragment = getFragmentManager().findFragmentByTag(progressDialogTag);
        if (fragment != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            try {
                ft.remove(fragment);
            } finally {
                if (!isSuspended()) {
                    ft.commitAllowingStateLoss();
                }
            }
        }
    }

    public boolean checkInternetConnection() {
        return getActivity() != null && ((BaseActivity) getActivity()).checkInternetConnection();
    }

    @Override
    public MessageHandler getHandler() {
        return handler;
    }


    /**
     * Return null if you don't want the title to be changed
     */

    public void showErrorMsg(String msg) {
        if (getCurrentActivity() != null) {
            getCurrentActivity().showAlertDialog(msg);
        }
    }

    @Nullable
    public abstract ViewGroup getContentView();

    public void showAlertDialog(CharSequence title,
                                CharSequence msg, @DialogButton.ButtonType int dialogButton,
                                @DialogButton.ButtonType int nxtDialogButton, final int requestCode,
                                final Bundle passedValue, String positiveBtnText) {
        if (getActivity() == null) return;
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
        ConfirmationDialogFragment dialogFragment = ConfirmationDialogFragment.newInstance(this,
                requestCode, title == null ? getString(R.string.app_name) : title, msg, positiveBtnText,
                negativeButtonText, passedValue, false);
        try {
            dialogFragment.show(getFragmentManager(), getScreenTag() + "#AlertDialog");
        } catch (IllegalStateException ex) {
            //Crashlytics.logException(ex);
        }
    }

    @Override
    public void onDialogConfirmed(int reqCode, Bundle data, boolean isPositive) {
        if (getCurrentActivity() == null) return;
        if (isPositive) {
            onPositiveButtonClicked(reqCode, data);
            if (data != null && data.getBoolean(Constants.FINISH_ACTIVITY, false)) {
                getCurrentActivity().finish();
            }
        } else {
            onNegativeButtonClicked(reqCode, data);
        }
    }


    @Override
    public void onDialogCancelled(int reqCode, Bundle data) {

    }

    protected void onPositiveButtonClicked(int sourceName, Bundle valuePassed) {
        if (getActivity() != null) {
            switch (sourceName) {
                default:

            }
        }
    }

    protected void onNegativeButtonClicked(int requestCode, Bundle data) {

    }

    public void showAlertDialogFinish(CharSequence title, CharSequence msg) {
        if (getCurrentActivity() == null || isSuspended()) return;

        Bundle data = new Bundle(2);
        data.putBoolean(Constants.FINISH_ACTIVITY, true);
        ConfirmationDialogFragment dialogFragment = ConfirmationDialogFragment.newInstance(
                this, 0, title == null ? getString(R.string.app_name) : title, msg,
                getString(R.string.ok),
                null, data, false);
        try {
            dialogFragment.show(getFragmentManager(), getScreenTag() + "#AlertDialog");
        } catch (IllegalStateException ex) {
            //Crashlytics.logException(ex);
        }
    }

    public abstract String getScreenTag();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            FragmentManager fragmentMgr = getFragmentManager();
            int backStackCount = fragmentMgr.getBackStackEntryCount();
            if (backStackCount > 0 && fragmentMgr.getBackStackEntryAt(backStackCount - 1) == this) {
                fragmentMgr.popBackStack();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showApiErrorDialog(@Nullable CharSequence title, CharSequence message) {
        if (getCurrentActivity() == null) return;
        getCurrentActivity().showAlertDialog(title, message);
    }

    @Override
    public void showApiErrorDialog(@Nullable CharSequence title, CharSequence message, boolean finish) {
        // Fix this implementation as fragment shouldn't finish activity
        if (getCurrentActivity() == null) return;
        if (finish) {
            showAlertDialogFinish(title, message);
        } else {
            getCurrentActivity().showAlertDialog(title, message);
        }
    }


    @Override
    public void showApiErrorDialog(@Nullable CharSequence title, CharSequence message, int requestCode, Bundle valuePassed) {
        if (getCurrentActivity() == null) return;
        showAlertDialog(title, message, DialogButton.OK, DialogButton.NONE, requestCode, valuePassed, null);
    }

    @Override
    public void showApiErrorDialog(@Nullable CharSequence title, CharSequence message, int resultCode) {
        if (getCurrentActivity() == null) return;
        getCurrentActivity().showAlertDialog(title, message, resultCode);
    }

}
