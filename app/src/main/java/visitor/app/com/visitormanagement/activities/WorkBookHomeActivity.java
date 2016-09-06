package visitor.app.com.visitormanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import retrofit2.Call;
import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.adapters.WorkBookHomePageAdapter;
import visitor.app.com.visitormanagement.interfaces.ApiService;
import visitor.app.com.visitormanagement.interfaces.NavigationCodes;
import visitor.app.com.visitormanagement.interfaces.OnWorkBookClickAware;
import visitor.app.com.visitormanagement.models.ApiResponse;
import visitor.app.com.visitormanagement.models.WorkBookModel;
import visitor.app.com.visitormanagement.service.UploadVisitorIntentService;
import visitor.app.com.visitormanagement.utils.ApiAdapter;
import visitor.app.com.visitormanagement.utils.Constants;
import visitor.app.com.visitormanagement.utils.NetworkCallback;

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
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getHomePageData() {
        if (!checkInternetConnection()) {
            getHandler().sendOfflineError(true);
            return;
        }

        showProgressDialog(getString(R.string.please_wait), true);
        ApiService apiService = ApiAdapter.getApiService(this);
        Call<ApiResponse<ArrayList<WorkBookModel>>> call = apiService.getHomePageData();
        call.enqueue(new NetworkCallback<ApiResponse<ArrayList<WorkBookModel>>>(this) {
            @Override
            public void onSuccess(ApiResponse<ArrayList<WorkBookModel>> workbookResponse) {
                hideProgressDialog();
                if (workbookResponse.status == 0) {
                    ArrayList<WorkBookModel> workBookModelArrayList = workbookResponse.apiResponseContent;
                    renderHomePageData(workBookModelArrayList);
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

    private void renderHomePageData(ArrayList<WorkBookModel> workBookModelArrayList) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        if (recyclerView == null ||  workBookModelArrayList == null) return;

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

    public void createWorkBook(View view) {
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
