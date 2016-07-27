package visitor.app.com.visitormanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import retrofit2.Call;
import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.adapters.VisitorListingRecyclerAdapter;
import visitor.app.com.visitormanagement.interfaces.ApiService;
import visitor.app.com.visitormanagement.interfaces.NavigationCodes;
import visitor.app.com.visitormanagement.models.ApiResponse;
import visitor.app.com.visitormanagement.models.VisitorModel;
import visitor.app.com.visitormanagement.models.WorkBookModel;
import visitor.app.com.visitormanagement.utils.ApiAdapter;
import visitor.app.com.visitormanagement.utils.Constants;
import visitor.app.com.visitormanagement.utils.NetworkCallback;
import visitor.app.com.visitormanagement.utils.UIUtil;

/**
 * Created by jugal on 22/7/16.
 */
public class VisitorListingActivity extends BaseActivity {

    private WorkBookModel workBookModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitor_listing_layout);
        workBookModel = getIntent().getParcelableExtra(Constants.VISITOR_INTENT);
        if(workBookModel == null) return;
        getVisitorData(workBookModel.getWbId());
    }

    private void getVisitorData(String wbId){
        if (!checkInternetConnection()) {
            getHandler().sendOfflineError(true);
            return;
        }

        showProgressDialog(getString(R.string.please_wait), true);
        ApiService apiService = ApiAdapter.getApiService(this);
        Call<ApiResponse<ArrayList<VisitorModel>>> call = apiService.getVisitors(wbId);
        call.enqueue(new NetworkCallback<ApiResponse<ArrayList<VisitorModel>>>(this) {
            @Override
            public void onSuccess(ApiResponse<ArrayList<VisitorModel>> workbookResponse) {
                hideProgressDialog();
                if (workbookResponse.status == 0) {
                    ArrayList<VisitorModel> visitorModelArrayList = workbookResponse.apiResponseContent;
                    renderVisitorListingPage(visitorModelArrayList);
                } else {
                    handler.sendEmptyMessage(workbookResponse.status, workbookResponse.message);
                }
            }

            @Override
            public boolean updateProgress() {
                try {
                    hideProgressDialog();
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }
        });
    }

    private void renderVisitorListingPage(ArrayList<VisitorModel> visitorModelArrayList){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        if(recyclerView == null) return;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        VisitorListingRecyclerAdapter visitorListingRecyclerAdapter = new VisitorListingRecyclerAdapter(visitorModelArrayList);
        recyclerView.setAdapter(visitorListingRecyclerAdapter);
    }


    public void onCreateVisitorBtnClicked(View view){
        if (workBookModel == null || UIUtil.isEmpty(workBookModel.getWbId())) return;
        Intent createWorkBookIntent = new Intent(this, CreateVisitorActivity.class);
        createWorkBookIntent.putExtra(Constants.WB_ID, workBookModel.getWbId());
        startActivityForResult(createWorkBookIntent, NavigationCodes.RC_GOTO_HOME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NavigationCodes.RC_CREATED_VISITOR && data != null &&
                !UIUtil.isEmpty(data.getStringExtra(Constants.WB_ID))) {
            getVisitorData(data.getStringExtra(Constants.WB_ID));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public String getScreenTag() {
        return getClass().getSimpleName();
    }
}
