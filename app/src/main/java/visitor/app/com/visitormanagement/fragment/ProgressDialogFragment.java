package visitor.app.com.visitormanagement.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.WindowManager;

/**
 * Fragment the displays progress dialog
 */
public class ProgressDialogFragment extends AppCompatDialogFragment {

    private static final String ARG_MSG = "com.bigbasket.mobileapp.fragment.base.ProgressDialogFragment.arg-msg";
    private static final String ARG_CANCELLABLE = "com.bigbasket.mobileapp.fragment.base.ProgressDialogFragment.arg-cancellable";
    private static final String ARG_DETERMINATE = "com.bigbasket.mobileapp.fragment.base.ProgressDialogFragment.arg-determinate";
    private ProgressDialog progressDialog;

    public static ProgressDialogFragment newInstance(String message, boolean cancellable, boolean determinate) {
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MSG, message);
        args.putBoolean(ARG_CANCELLABLE, cancellable);
        args.putBoolean(ARG_DETERMINATE, determinate);
        fragment.setArguments(args);
        return fragment;
    }

    public ProgressDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        boolean error = false;
        try {
            super.onStart();
        } catch (WindowManager.BadTokenException t) {
            //Crashlytics.logException(t);
            error = true;
        }
        if (error) {
            try {
                dismissAllowingStateLoss();
            } catch (Exception ex) {
                //Ignore
            }
            return;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(getActivity());
        boolean isDeterminate = false;
        String message = null;
        boolean cancellable = true;
        if (getArguments() != null) {
            message = getArguments().getString(ARG_MSG);
            cancellable = getArguments().getBoolean(ARG_CANCELLABLE, true);
            isDeterminate = getArguments().getBoolean(ARG_DETERMINATE, false);
        }
        if (isDeterminate) {
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressNumberFormat(null);
            progressDialog.setProgressPercentFormat(null);
        }
        progressDialog.setCancelable(cancellable);
        progressDialog.setCanceledOnTouchOutside(cancellable);
        setCancelable(cancellable);
        progressDialog.setMessage(message);
        return progressDialog;
    }

}
