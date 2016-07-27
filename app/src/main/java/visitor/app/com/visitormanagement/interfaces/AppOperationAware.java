package visitor.app.com.visitormanagement.interfaces;


import visitor.app.com.visitormanagement.activities.BaseActivity;
import visitor.app.com.visitormanagement.utils.MessageHandler;

public interface AppOperationAware {
    BaseActivity getCurrentActivity();

    boolean isSuspended();

    void setSuspended(boolean state);

    boolean checkInternetConnection();

    MessageHandler getHandler();

    void showProgressDialog(String msg);

    void showProgressDialog(String msg, boolean cancelable);

    void showProgressDialog(String msg, boolean cancelable, boolean isDeterminate);

    void hideProgressDialog();

    void showProgressView();

    void hideProgressView();
}
