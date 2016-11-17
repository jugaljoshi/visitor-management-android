package visitor.app.com.visitormanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.adapters.VisitorListingRecyclerAdapter;
import visitor.app.com.visitormanagement.database.CreateVisitorHelper;
import visitor.app.com.visitormanagement.database.CreateVisitorObjHelper;
import visitor.app.com.visitormanagement.interfaces.ApiErrorCodes;
import visitor.app.com.visitormanagement.interfaces.ApiService;
import visitor.app.com.visitormanagement.interfaces.NavigationCodes;
import visitor.app.com.visitormanagement.models.ApiResponse;
import visitor.app.com.visitormanagement.models.VisitorModel;
import visitor.app.com.visitormanagement.utils.ApiAdapter;
import visitor.app.com.visitormanagement.utils.Constants;
import visitor.app.com.visitormanagement.utils.NetworkCallback;
import visitor.app.com.visitormanagement.utils.UIUtil;

/**
 * Created by jugal on 22/7/16.
 */
public class VisitorListingActivity extends BaseActivity {

    private String wbId;
    private ArrayList<String> visitorMandatoryFields;
    private RelativeLayout layoutVisitorListingPage;
    private LinearLayout emptyLayoutView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitor_listing_layout);
        layoutVisitorListingPage = (RelativeLayout) findViewById(R.id.layoutVisitorListingPage);
        emptyLayoutView = (LinearLayout) findViewById(R.id.emptyLayoutView);
        FloatingActionButton floatingActionBtn = (FloatingActionButton) findViewById(R.id.floatingActionBtn);
        ArrayList<VisitorModel> visitorModelArrayList = getIntent().getParcelableArrayListExtra(Constants.VISITORS);
        if (visitorModelArrayList != null && visitorModelArrayList.size() > 0) { // via search
            renderVisitorListingPage(visitorModelArrayList);
            floatingActionBtn.setVisibility(View.GONE);
        } else { // via home page
            wbId = getIntent().getStringExtra(Constants.WB_ID);
            visitorMandatoryFields = getIntent().getStringArrayListExtra(Constants.VISITOR_MANDATORY_FIELDS);
            if (UIUtil.isEmpty(wbId)) return;
            if (visitorMandatoryFields == null || visitorMandatoryFields.size() <= 0) {
                floatingActionBtn.setVisibility(View.GONE);
            }
            getVisitorData(wbId);
        }
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

    private void showEmptyVisitorPage(){
        layoutVisitorListingPage.setVisibility(View.GONE);
        emptyLayoutView.setVisibility(View.VISIBLE);
        UIUtil.getEmptyPageView(emptyLayoutView, VisitorListingActivity.this, true);
    }

    private ArrayList<VisitorModel> getStoredVisitorsFromDB(){
        ArrayList<VisitorModel> visitorModelArrayList = new ArrayList<>();

        ArrayList<CreateVisitorObjHelper> visitorObjHelpers = CreateVisitorHelper.getVisitorRecords(this, -1);
        if(visitorObjHelpers == null || visitorObjHelpers.isEmpty()) return null;
        Gson gson = new GsonBuilder().create();
        Type hashMapType = new TypeToken<HashMap<String, String>>() {}.getType();
        for (CreateVisitorObjHelper visitorObjHelper : visitorObjHelpers) {
            HashMap<String, String> payload = gson.fromJson(visitorObjHelper.getParams(), hashMapType);
            VisitorModel visitorModel = new VisitorModel();
            if (payload.containsKey(Constants.WB_ID)) {
                if (!payload.get(Constants.WB_ID).equalsIgnoreCase(wbId)) {
                    continue;
                }
                visitorModel.setWbId(payload.get(Constants.WB_ID));
            } else {
                continue;
            }

            String visitorImg = visitorObjHelper.getvPhotoUrl();
            if (!UIUtil.isEmpty(visitorImg)) visitorModel.setVisitorImg(visitorImg);
            String signatureUrl = visitorObjHelper.getvSignatureUrl();
            if (!UIUtil.isEmpty(signatureUrl)) visitorModel.setVisitorSignUrl(signatureUrl);


            if (payload.containsKey(Constants.NAME))
                visitorModel.setName(payload.get(Constants.NAME));
            if (payload.containsKey(Constants.MOBILE_NO))
                visitorModel.setMobileNumber(payload.get(Constants.MOBILE_NO));
            if (payload.containsKey(Constants.VEHICLE_NO))
                visitorModel.setVehicleNo(payload.get(Constants.VEHICLE_NO));
            if (payload.containsKey(Constants.FROM_PLACE))
                visitorModel.setFromPlace(payload.get(Constants.FROM_PLACE));
            if (payload.containsKey(Constants.DESTINATION_PLACE))
                visitorModel.setDestinationPlace(payload.get(Constants.DESTINATION_PLACE));
            if (payload.containsKey(Constants.IN_TIME)) {
                String dateTime = payload.get(Constants.IN_TIME); //06122016 12:34:00
                String displayTime = "";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy HH:mm:ss", Locale.getDefault());
                try {
                    Date date = simpleDateFormat.parse(dateTime);
                    displayTime = date.getHours() + ":" + date.getMinutes();
                } catch (ParseException ex) {
                    System.out.println("Exception " + ex);
                    displayTime = dateTime.split(" ")[1];
                }
                visitorModel.setInTime(displayTime);
            }
            if (payload.containsKey(Constants.OUT_TIME)) {
                String dateTime = payload.get(Constants.OUT_TIME);
                String displayTime = "";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy HH:mm:ss", Locale.getDefault());
                try {
                    Date date = simpleDateFormat.parse(dateTime);
                    displayTime = date.getHours() + ":" + date.getMinutes();
                } catch (ParseException ex) {
                    System.out.println("Exception " + ex);
                    displayTime = dateTime.split(" ")[1];
                }
                visitorModel.setOutTime(displayTime);
            }

            visitorModelArrayList.add(visitorModel);
        }
        return visitorModelArrayList;
    }

    private void getVisitorData(String wbId){
        if (!checkInternetConnection()) {
            showToast(getString(R.string.no_network_label));
            ArrayList<VisitorModel> visitorStoredModelArrayList = getStoredVisitorsFromDB();
            if (visitorStoredModelArrayList == null || visitorStoredModelArrayList.isEmpty()) {
                showEmptyVisitorPage();
            } else {
                renderVisitorListingPage(visitorStoredModelArrayList);
            }
            return;
        }

        showProgressDialog(getString(R.string.please_wait), true);
        ApiService apiService = ApiAdapter.getApiService(this);
        String queryName = getIntent().getStringExtra(Constants.NAME);
        Call<ApiResponse<ArrayList<VisitorModel>>> call = apiService.getVisitors(wbId, queryName);
        call.enqueue(new NetworkCallback<ApiResponse<ArrayList<VisitorModel>>>(this) {
            @Override
            public void onSuccess(ApiResponse<ArrayList<VisitorModel>> workbookResponse) {
                hideProgressDialog();
                if (workbookResponse.status == 0) {
                    ArrayList<VisitorModel> visitorModelArrayList = workbookResponse.apiResponseContent;
                    renderVisitorListingPage(visitorModelArrayList);
                } else if(workbookResponse.status == ApiErrorCodes.NO_VISITOR){
                    showEmptyVisitorPage();
                }else {
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
        layoutVisitorListingPage.setVisibility(View.VISIBLE);
        emptyLayoutView.setVisibility(View.GONE);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        if(recyclerView == null) return;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        VisitorListingRecyclerAdapter visitorListingRecyclerAdapter = new VisitorListingRecyclerAdapter(visitorModelArrayList, this);
        recyclerView.setAdapter(visitorListingRecyclerAdapter);
    }


    public void bottomBtmClicked(View view){
        if (UIUtil.isEmpty(wbId)) return;
        Intent createWorkBookIntent = new Intent(this, CreateVisitorActivity.class);
        createWorkBookIntent.putExtra(Constants.WB_ID, wbId);
        createWorkBookIntent.putStringArrayListExtra(Constants.VISITOR_MANDATORY_FIELDS, visitorMandatoryFields);
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
