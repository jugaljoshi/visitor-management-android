package visitor.app.com.visitormanagement.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.database.WorkbookHelper;
import visitor.app.com.visitormanagement.fragment.CreateWorkBookDialog;
import visitor.app.com.visitormanagement.interfaces.ApiService;
import visitor.app.com.visitormanagement.interfaces.NavigationCodes;
import visitor.app.com.visitormanagement.interfaces.OnWorkBookTypeSubmitBtnClickAware;
import visitor.app.com.visitormanagement.models.ApiResponse;
import visitor.app.com.visitormanagement.models.WorkBookModel;
import visitor.app.com.visitormanagement.models.WorkBookResponse;
import visitor.app.com.visitormanagement.models.WorkBookTypeModel;
import visitor.app.com.visitormanagement.utils.ApiAdapter;
import visitor.app.com.visitormanagement.utils.Constants;
import visitor.app.com.visitormanagement.utils.DialogButton;
import visitor.app.com.visitormanagement.utils.NetworkCallback;
import visitor.app.com.visitormanagement.utils.UIUtil;

/**
 * Created by jugal on 22/7/16.
 */
public class CreateWorkBookActivity extends BaseActivity implements OnWorkBookTypeSubmitBtnClickAware {

    private static final int REQUEST_STORE_LOCAL_DB = 100;
    private EditText editTxtWorkBookName;
    private String workBookTypeId;
    private ArrayList<String> selectedFieldIds = new ArrayList<>();
    private LinearLayout emptyLayoutView, layoutWorkbookType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_workbook_layout);
        emptyLayoutView = (LinearLayout) findViewById(R.id.emptyLayoutView);
        layoutWorkbookType = (LinearLayout) findViewById(R.id.layoutWorkbookType);
        getWBTypes();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_workbook_type, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_workbook_type:
                renderCreateWorkBookDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void renderCreateWorkBookDialog(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = getSupportFragmentManager().findFragmentByTag("create_workbook_type");
        if (f == null) {
            CreateWorkBookDialog createWorkBookDialog = CreateWorkBookDialog.newInstance();
            try {
                createWorkBookDialog.show(ft, "create_workbook_type");
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onWorkBookTypeSubmitBtnClickAware(String workBookTypeName) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CreateWorkBookActivity.this);
        final Gson gson = new Gson();
        if (!checkInternetConnection()) {
            showAlertDialog(getString(R.string.no_network_label_for_workbook_type));
            //showToast(getString(R.string.no_network_label));
//            String workBookResponseString = sharedPreferences.getString(Constants.WORK_BOOK_RESPONSE, null);
//            if(UIUtil.isEmpty(workBookResponseString)){
//                WorkBookResponse workBookResponse = gson.fromJson(workBookResponseString, WorkBookResponse.class);
//                renderCreateWorkBook(workBookResponse);
//            }
            return;
        }
        showProgressDialog(getString(R.string.please_wait), true);
        ApiService apiService = ApiAdapter.getApiService(this);
        Call<ApiResponse<WorkBookResponse>> call = apiService.createWorkBookType(workBookTypeName);
        call.enqueue(new NetworkCallback<ApiResponse<WorkBookResponse>>(this) {
            @Override
            public void onSuccess(ApiResponse<WorkBookResponse> workbookTypeResponse) {
                hideProgressDialog();
                if (workbookTypeResponse.status == 0) {
                    WorkBookResponse workBookResponse = workbookTypeResponse.apiResponseContent;
                    renderCreateWorkBook(workBookResponse);
                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                    String workBookResponseString  = gson.toJson(workBookResponse);
                    editor.putString(Constants.WORK_BOOK_RESPONSE, workBookResponseString);
                    editor.apply();
                } else {
                    showToast(workbookTypeResponse.message);
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

    private void renderEmptyPage(LinearLayout emptyLayoutView, LinearLayout layoutWorkbookType){
        UIUtil.getEmptyPageView(emptyLayoutView, this, false);
        layoutWorkbookType.setVisibility(View.GONE);
        emptyLayoutView.setVisibility(View.VISIBLE);
    }

    private void getWBTypes() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final Gson gson = new Gson();
        if (!checkInternetConnection()) {
            String workBookResponseString = sharedPreferences.getString(Constants.WORK_BOOK_RESPONSE, null);
            if (UIUtil.isEmpty(workBookResponseString)) {
                //getHandler().sendOfflineError(true);
                renderEmptyPage(emptyLayoutView, layoutWorkbookType);
                return;
            } else {
                showToast(getString(R.string.no_network_label));
                WorkBookResponse workBookResponse = gson.fromJson(workBookResponseString, WorkBookResponse.class);
                renderCreateWorkBook(workBookResponse);
                return;
            }
        }

        final SharedPreferences.Editor editor = sharedPreferences.edit();
        showProgressDialog(getString(R.string.please_wait), true);
        ApiService apiService = ApiAdapter.getApiService(this);
        Call<ApiResponse<WorkBookResponse>> call = apiService.getWorkBookTypes();
        call.enqueue(new NetworkCallback<ApiResponse<WorkBookResponse>>(this) {
            @Override
            public void onSuccess(ApiResponse<WorkBookResponse> workbookTypeResponse) {
                hideProgressDialog();
                if (workbookTypeResponse.status == 0) {
                    WorkBookResponse workBookResponse = workbookTypeResponse.apiResponseContent;
                    renderCreateWorkBook(workBookResponse);
                    String workBookResponseString  = gson.toJson(workBookResponse);
                    editor.putString(Constants.WORK_BOOK_RESPONSE, workBookResponseString);
                    editor.apply();
                } else {
                    handler.sendEmptyMessage(workbookTypeResponse.status, workbookTypeResponse.message, true);
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

    private void renderCreateWorkBook(WorkBookResponse workBookResponse) {
        if(workBookResponse == null) renderEmptyPage(emptyLayoutView, layoutWorkbookType);

        final ArrayList<WorkBookTypeModel> workBookTypeModelArrayList = workBookResponse.getWorkBookTypeModelArrayList();
        ArrayList<String> visitorMandatoryFields = workBookResponse.getVisitorMandatoryFields();

        if (workBookTypeModelArrayList == null || workBookTypeModelArrayList.size() == 0 ||
                visitorMandatoryFields == null || visitorMandatoryFields.size() == 0) {
            renderEmptyPage(emptyLayoutView, layoutWorkbookType);
            //handler.sendEmptyMessage(ApiErrorCodes.GENERIC_ERROR, getString(R.string.error_while_getting_data), true);
            return;
        } else {
            layoutWorkbookType.setVisibility(View.VISIBLE);
            emptyLayoutView.setVisibility(View.GONE);
        }

        editTxtWorkBookName = (EditText) findViewById(R.id.editTxtWorkBookName);

        // set spinner data
        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(0, "--- Select Type ---");
        for (WorkBookTypeModel workBookTypeModel : workBookTypeModelArrayList) {
            stringArrayList.add(workBookTypeModel.getWbType());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, stringArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinnerWorkBookType = (Spinner) findViewById(R.id.spinnerWorkBookType);
        if (spinnerWorkBookType == null) return;
        spinnerWorkBookType.setAdapter(arrayAdapter);
        spinnerWorkBookType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position <= 0) {
                    return;
                }
                WorkBookTypeModel workBookTypeModel = workBookTypeModelArrayList.get(position - 1);
                workBookTypeId = workBookTypeModel.getWbTypeId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //set visitor mandatory fields data
        renderMandatoryFieldsView(visitorMandatoryFields);
    }

    private void renderMandatoryFieldsView(ArrayList<String> visitorMandatoryFields) {
        LinearLayout layoutMandatoryFields = (LinearLayout) findViewById(R.id.layoutMandatoryFields);
        if (layoutMandatoryFields == null) return;
        layoutMandatoryFields.removeAllViews();

        final int black = ContextCompat.getColor(this, R.color.dark_black);
        int margin = getResources().getDimensionPixelSize(R.dimen.margin_small);
        int padding = getResources().getDimensionPixelSize(R.dimen.padding_small);

        MandatoryButtonOnClickListener reasonsButtonOnClickListener = new MandatoryButtonOnClickListener(this);
        int mandatoryFieldsSize = visitorMandatoryFields.size();
        int n = mandatoryFieldsSize % 2 == 0 ? mandatoryFieldsSize : mandatoryFieldsSize - 1;

        for (int i = 0; i < n; i++) {

            // Addling linear horizontal
            LinearLayout layoutHorizontal = createLinerHorizontalLayout();

            // Adding button 1
            final Button button = createButton(visitorMandatoryFields.get(i), margin, padding,
                    black, reasonsButtonOnClickListener);
            i++;

            // Adding button 2
            Button buttonSecond = createButton(visitorMandatoryFields.get(i), margin, padding,
                    black, reasonsButtonOnClickListener);

            layoutHorizontal.addView(button);
            layoutHorizontal.addView(buttonSecond);
            layoutMandatoryFields.addView(layoutHorizontal);
        }

        // Button if the reasons size is not even
        if (mandatoryFieldsSize % 2 > 0) {
            int val = mandatoryFieldsSize - 1;
            LinearLayout layoutHorizontal = createLinerHorizontalLayout();

            // Add Button 3
            Button buttonThird = createButton(visitorMandatoryFields.get(val), margin, padding,
                    black, reasonsButtonOnClickListener);

            // Add Dumm View
            View dummyView = new View(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            layoutParams.setMargins(margin, margin, margin, margin);
            dummyView.setLayoutParams(layoutParams);

            layoutHorizontal.addView(buttonThird);
            layoutHorizontal.addView(dummyView);
            layoutMandatoryFields.addView(layoutHorizontal);
        }
    }

    private LinearLayout createLinerHorizontalLayout() {
        LinearLayout layoutHorizontal = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutHorizontal.setLayoutParams(layoutParams);
        layoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
        return layoutHorizontal;
    }

    private Button createButton(String mandatoryField, int margin, int padding, int colorBlack,
                                View.OnClickListener buttonOnClickListener) {
        Button button = new Button(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        layoutParams.setMargins(margin, margin, margin, margin);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        button.setLayoutParams(layoutParams);
        button.setGravity(Gravity.CENTER);
        //button.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        button.setPadding(padding, padding, padding, padding);
        button.setTextColor(colorBlack);
        button.setAllCaps(true);
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.grey_f1));
        button.setTag(R.id.field_name, mandatoryField);
        button.setText(mandatoryField);
        button.setOnClickListener(buttonOnClickListener);
        return button;
    }

    public void onCreateWorkBookBtnClicked(View view) {
        editTxtWorkBookName.setError(null);

        boolean cancel = false;
        View focusView = null;
        String workbookName = editTxtWorkBookName.getText().toString();
        if (TextUtils.isEmpty(workbookName)) {
            editTxtWorkBookName.setError(getString(R.string.error_field_required));
            focusView = editTxtWorkBookName;
            cancel = true;
        }

        if (TextUtils.isEmpty(workBookTypeId)) {
            showToast(getString(R.string.select_workbook_type));
        }

        if (cancel) {
            focusView.requestFocus();
        } else if (selectedFieldIds == null || selectedFieldIds.size() == 0) {
            showToast(getString(R.string.select_visitor_mandatory));
        } else {
            createWorkBook(workbookName.trim(), workBookTypeId, TextUtils.join(",", selectedFieldIds));
        }
    }

    private void storeDataToLocalDBToUpload(Bundle data) {
        String workBookName = data.getString(Constants.WB_NAME);
        String workBookTypeId = data.getString(Constants.WB_TYPE_ID);
        String visitorMandatoryFields = data.getString(Constants.VISITOR_MANDATORY_FIELDS);
        WorkbookHelper.update(this, workBookName, workBookTypeId, visitorMandatoryFields, 1);
        showToast(getString(R.string.record_inserted));
        onWorkBookCreated();
    }

    @Override
    protected void onPositiveButtonClicked(int sourceName, Bundle valuePassed) {
        if (sourceName == REQUEST_STORE_LOCAL_DB) {
            storeDataToLocalDBToUpload(valuePassed);
        } else {
            super.onPositiveButtonClicked(sourceName, valuePassed);
        }
    }

    private void createWorkBook(String workBookName, String workBookId, String commaSeparatedMandatoryFields) {
        if (!checkInternetConnection()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.WB_NAME, workBookName);
            bundle.putString(Constants.WB_TYPE_ID, workBookId);
            bundle.putString(Constants.VISITOR_MANDATORY_FIELDS, new Gson().toJson(selectedFieldIds));
            showAlertDialog(getString(R.string.store_to_local_db_title), getString(R.string.store_to_local_db),
                    DialogButton.YES, DialogButton.CANCEL, REQUEST_STORE_LOCAL_DB, bundle, getString(R.string.insert));
            return;
        }

        showProgressDialog(getString(R.string.please_wait), true);
        ApiService apiService = ApiAdapter.getApiService(this);
        Call<ApiResponse<ArrayList<WorkBookModel>>> call = apiService.getCreateWorkbook(workBookName, workBookId,
                commaSeparatedMandatoryFields);
        call.enqueue(new NetworkCallback<ApiResponse<ArrayList<WorkBookModel>>>(this) {
            @Override
            public void onSuccess(ApiResponse<ArrayList<WorkBookModel>> workbookResponse) {
                hideProgressDialog();
                if (workbookResponse.status == 0) {
                    onWorkBookCreated();
                } else {
                    handler.sendEmptyMessage(workbookResponse.status,
                            workbookResponse.message);
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

    private void onWorkBookCreated() {
        setResult(NavigationCodes.RC_CREATED_WORKBOOK);
        finish();
    }

    @Override
    public String getScreenTag() {
        return getClass().getSimpleName();
    }

    /*
    private class WorkBookTypeAdapter extends ArrayAdapter<WorkBookModel>{

        public WorkBookTypeAdapter(Context context, int resource, List objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            if (view instanceof TextView) {
                ((TextView) view).setText();
                ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return super.getDropDownView(position, convertView, parent);
        }

    }


    */

    private class MandatoryButtonOnClickListener implements View.OnClickListener {

        int black;
        int white;
        private Context mContext;

        public MandatoryButtonOnClickListener(Context context) {
            mContext = context;
            black = ContextCompat.getColor(mContext, R.color.dark_black);
            white = ContextCompat.getColor(mContext, R.color.white);
        }

        @Override
        public void onClick(View v) {
            Button btn = (Button) v;
            String key = (String) btn.getTag(R.id.field_name);
            if (selectedFieldIds != null) {
                if (selectedFieldIds.contains(key)) {
                    btn.setBackgroundColor(ContextCompat.getColor(mContext,
                            R.color.grey_f1));
                    btn.setTextColor(black);
                    selectedFieldIds.remove(key);
                } else {
                    btn.setBackgroundColor(ContextCompat.getColor(mContext,
                            R.color.colorPrimary));

                    btn.setTextColor(white);
                    selectedFieldIds.add(key);
                }
            }
        }
    }
}
