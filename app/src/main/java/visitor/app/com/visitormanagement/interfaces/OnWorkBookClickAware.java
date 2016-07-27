package visitor.app.com.visitormanagement.interfaces;

import visitor.app.com.visitormanagement.models.WorkBookModel;

/**
 * Created by jugal on 22/7/16.
 */
public interface OnWorkBookClickAware {
    void onWorkBookItemClicked(WorkBookModel workBookModel);
}
