package visitor.app.com.visitormanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.adapters.WorkBookHomePageAdapter;
import visitor.app.com.visitormanagement.database.WorkbookHelper;
import visitor.app.com.visitormanagement.interfaces.ApiService;
import visitor.app.com.visitormanagement.interfaces.NavigationCodes;
import visitor.app.com.visitormanagement.interfaces.OnWorkBookClickAware;
import visitor.app.com.visitormanagement.models.ApiResponse;
import visitor.app.com.visitormanagement.models.WorkBookModel;
import visitor.app.com.visitormanagement.service.UploadVisitorIntentService;
import visitor.app.com.visitormanagement.utils.ApiAdapter;
import visitor.app.com.visitormanagement.utils.Constants;
import visitor.app.com.visitormanagement.utils.NetworkCallback;
import visitor.app.com.visitormanagement.utils.UIUtil;

/**
 * Created by jugal on 16/7/16.
 */
public class WorkBookHomeActivity extends BaseActivity implements OnWorkBookClickAware {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page_layout);
        getHomePageData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, UploadVisitorIntentService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, SearchByFieldActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getHomePageData() {
        final ArrayList<WorkBookModel> workBookModelArrayList = WorkbookHelper.getVisitorRecords(this, -1);
        if (!checkInternetConnection()) {
            showToast(getString(R.string.no_network_label));
            // get data from data base where uploaded is 0
            renderHomePageData(workBookModelArrayList);
            return;
        }

        showProgressDialog(getString(R.string.please_wait), true);

        final HashMap<String, WorkBookModel> workBookStoredIdHashMap = new HashMap<>();
        if(workBookModelArrayList != null && workBookModelArrayList.isEmpty()){
            for(WorkBookModel workBookModel : workBookModelArrayList){
                workBookStoredIdHashMap.put(workBookModel.getWbId(), workBookModel);
            }
        }

        ApiService apiService = ApiAdapter.getApiService(this);
        Call<ApiResponse<ArrayList<WorkBookModel>>> call = apiService.getHomePageData();
        call.enqueue(new NetworkCallback<ApiResponse<ArrayList<WorkBookModel>>>(this) {
            @Override
            public void onSuccess(ApiResponse<ArrayList<WorkBookModel>> workbookResponse) {
                hideProgressDialog();
                if (workbookResponse.status == 0) {
                    ArrayList<WorkBookModel> workBookModelResponseArrayList = workbookResponse.apiResponseContent;
                    if (workBookModelResponseArrayList != null) {
                        for (WorkBookModel workBookModelResponse : workBookModelResponseArrayList) {
                            if (workBookStoredIdHashMap.containsKey(workBookModelResponse.getWbId())) {
                                // Remove this model as this model is present in server response
                                workBookStoredIdHashMap.remove(workBookModelResponse.getWbId());
                            }
                        }
                        workBookModelResponseArrayList.addAll(workBookStoredIdHashMap.values());
                        // store new response to DB
                        storeDataToDB(workBookModelResponseArrayList);
                    }

                    renderHomePageData(workBookModelResponseArrayList);
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

    private void storeDataToDB(ArrayList<WorkBookModel> workBookModelArrayList) {
        WorkbookHelper.deleteTableData(this);
        for(WorkBookModel workBookModel : workBookModelArrayList){
            WorkbookHelper.update(this, workBookModel.getWbName(), workBookModel.getWbId(),
                    new Gson().toJson(workBookModel.getVisitorMandatoryFields()), 0);
        }
    }

    private void renderHomePageData(ArrayList<WorkBookModel> workBookModelArrayList) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayout emptyLayoutView = (LinearLayout) findViewById(R.id.emptyLayoutView);
        RelativeLayout layoutHomePage = (RelativeLayout) findViewById(R.id.layoutHomePage);
        if (recyclerView == null || emptyLayoutView == null || layoutHomePage == null) return;

        if (workBookModelArrayList == null || workBookModelArrayList.isEmpty()) {
            UIUtil.getEmptyPageView(emptyLayoutView, this, true);
            layoutHomePage.setVisibility(View.GONE);
            emptyLayoutView.setVisibility(View.VISIBLE);
            return;
        } else {
            layoutHomePage.setVisibility(View.VISIBLE);
            emptyLayoutView.setVisibility(View.GONE);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        WorkBookHomePageAdapter homePageAdapter = new WorkBookHomePageAdapter<>(this, workBookModelArrayList);
        recyclerView.setAdapter(homePageAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public void onWorkBookItemClicked(WorkBookModel workBookModel) {
        Intent visitorListingIntent = new Intent(this, VisitorListingActivity.class);
        visitorListingIntent.putExtra(Constants.WB_ID, workBookModel.getWbId());
        visitorListingIntent.putStringArrayListExtra(Constants.VISITOR_MANDATORY_FIELDS, workBookModel.getVisitorMandatoryFields());
        startActivity(visitorListingIntent);
    }

    public void bottomBtmClicked(View view) {
        Intent createWorkBookIntent = new Intent(this, CreateWorkBookActivity.class);
        startActivityForResult(createWorkBookIntent, NavigationCodes.RC_GOTO_HOME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NavigationCodes.RC_CREATED_WORKBOOK) {
            getHomePageData();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public String getScreenTag() {
        return getClass().getSimpleName();
    }
}
