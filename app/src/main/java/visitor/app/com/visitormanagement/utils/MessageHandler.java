package visitor.app.com.visitormanagement.utils;

import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import java.net.HttpURLConnection;

import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.application.BaseApplication;
import visitor.app.com.visitormanagement.interfaces.ApiErrorAware;
import visitor.app.com.visitormanagement.interfaces.ApiErrorCodes;
import visitor.app.com.visitormanagement.interfaces.AppOperationAware;
import visitor.app.com.visitormanagement.interfaces.NavigationCodes;

public class MessageHandler<T extends ApiErrorAware & AppOperationAware> {

    private T ctx;

    public MessageHandler(T ctx) {
        this.ctx = ctx;
    }

    public void sendEmptyMessage(int what, String message, boolean finish) {

        switch (what) {
            case ApiErrorCodes.EMAIL_ALREADY_EXISTS:
                ctx.showApiErrorDialog(null, !TextUtils.isEmpty(message) ? message : getString(R.string.REGISTERED_EMAIL));
                break;
            case ApiErrorCodes.GENERIC_ERROR:
                ctx.showApiErrorDialog(null, !TextUtils.isEmpty(message) ? message : getString(R.string.server_error), finish);
                break;
            case ApiErrorCodes.INTERNAL_SERVER_ERROR:
                ctx.showApiErrorDialog(getString(R.string.headingServerError),
                        getString(R.string.server_error), finish);
                break;
            case ApiErrorCodes.INVALID_USER:
                ctx.showApiErrorDialog(null,
                        !TextUtils.isEmpty(message) ? message : getString(R.string.invalid_user),
                        false);
                break;
            case ApiErrorCodes.INVALID_FIELD:
                ctx.showApiErrorDialog(getString(R.string.headingServerError),
                        getString(R.string.server_error), true);
                break;

            case ApiErrorCodes.LOGIN_REQUIRED:
                ctx.showApiErrorDialog(getString(R.string.signIn), getString(R.string.login_required), NavigationCodes.GO_TO_LOGIN, null);
                break;
            case ApiErrorCodes.EMAIL_DOESNT_EXISTS:
                ctx.showApiErrorDialog(null, message, finish);
                break;
            default:
                ctx.showApiErrorDialog(null,
                        !TextUtils.isEmpty(message) ? message : getString(R.string.server_error), finish);
                break;
        }
    }

    public void sendEmptyMessage(int what, String message) {
        sendEmptyMessage(what, message, false);
    }

    private String getString(@StringRes int resId) {
        return ctx.getCurrentActivity() != null ? ctx.getCurrentActivity().getString(resId)
                : BaseApplication.getContext().getString(resId);
    }

    public void handleRetrofitError(Throwable t, boolean finish) {
        ctx.showApiErrorDialog(getString(R.string.headingNetworkError),
                getString(R.string.msgNetworkError), finish);
    }

    public void handleHttpError(int errorCode, String reasonPhrase, boolean finish) {
        if (reasonPhrase == null) {
            ctx.showApiErrorDialog(getString(R.string.headingNetworkError),
                    getString(R.string.msgNetworkError), finish);
            return;
        }
        if (errorCode == HttpURLConnection.HTTP_UNAVAILABLE) {
            if (!finish) {
                ctx.showApiErrorDialog(getString(R.string.weAreDown),
                        getString(R.string.serviceUnavailable), 0, null);
            } else {
                ctx.getCurrentActivity().showAlertDialogFinish(getString(R.string.weAreDown),
                        getString(R.string.serviceUnavailable));
            }
        } else if (errorCode == HttpURLConnection.HTTP_BAD_GATEWAY || errorCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            if (!finish) {
                ctx.showApiErrorDialog(getString(R.string.headingServerError),
                        getString(R.string.server_error), 0, null);
            } else {
                ctx.getCurrentActivity().showAlertDialogFinish(getString(R.string.headingServerError),
                        getString(R.string.server_error));
            }
        } else if (errorCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            showUnauthorised();
        } else {
            String msg = "HTTP " + errorCode + " : " + reasonPhrase;
            if (!finish) {
                ctx.showApiErrorDialog(null, msg, 0, null);
            } else {
                ctx.showApiErrorDialog(null, msg, true);
            }
        }
    }

    private void showUnauthorised() {
        ctx.showApiErrorDialog(getString(R.string.signIn),
                getString(R.string.login_required), NavigationCodes.GO_TO_LOGIN, null);
    }

    public void sendOfflineError() {
        sendOfflineError(false);
    }

    public void sendOfflineError(boolean finish) {
        ctx.showApiErrorDialog(getString(R.string.headingConnectionOffline),
                getString(R.string.connectionOffline), finish);
    }
}
