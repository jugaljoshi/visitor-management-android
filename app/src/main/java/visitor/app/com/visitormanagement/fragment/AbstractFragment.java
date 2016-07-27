package visitor.app.com.visitormanagement.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import visitor.app.com.visitormanagement.activities.BaseActivity;
import visitor.app.com.visitormanagement.interfaces.AppOperationAware;
import visitor.app.com.visitormanagement.utils.Constants;


public abstract class AbstractFragment extends Fragment implements AppOperationAware {

    private boolean mAlreadyLoaded = false;
    private boolean isFragmentSuspended;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mAlreadyLoaded = savedInstanceState.getBoolean(Constants.FRAGMENT_STATE);
        }
        isFragmentSuspended = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isFragmentSuspended = false;
    }

    public void onBackStateChanged() {
        isFragmentSuspended = false;
        if (mAlreadyLoaded) {
            onBackResume();
        } else {
            mAlreadyLoaded = true;
        }
    }

    protected void onBackResume() {
        isFragmentSuspended = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentSuspended = true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isFragmentSuspended = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        isFragmentSuspended = false;
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isSuspended() {
        return getActivity() != null && ((BaseActivity) getActivity()).isSuspended() || isFragmentSuspended;
    }

    public void setSuspended(boolean isFragmentSuspended) {
        this.isFragmentSuspended = isFragmentSuspended;
    }

    @Override
    public BaseActivity getCurrentActivity() {
        return (BaseActivity) getActivity();
    }

    protected void finish() {
        if (getActivity() != null) {
            try {
                int backStackCount = getFragmentManager().getBackStackEntryCount();
                if (backStackCount > 0
                        && getFragmentManager().getBackStackEntryAt(backStackCount - 1) == this) {
                    getFragmentManager().popBackStack();
                } else {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.remove(this);
                    ft.commit();
                }
            } catch (IllegalStateException ex) {
                ///Crashlytics.logException(ex);
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(Constants.FRAGMENT_STATE, mAlreadyLoaded);
        super.onSaveInstanceState(outState);
    }

    @NonNull
    public abstract String getFragmentTxnTag();
}