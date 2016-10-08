package visitor.app.com.visitormanagement.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.interfaces.ApiService;
import visitor.app.com.visitormanagement.models.ApiResponse;
import visitor.app.com.visitormanagement.models.VisitorModel;
import visitor.app.com.visitormanagement.utils.ApiAdapter;
import visitor.app.com.visitormanagement.utils.Constants;
import visitor.app.com.visitormanagement.utils.NetworkCallback;
import visitor.app.com.visitormanagement.utils.UIUtil;

/**
 * Created by jugal on 21/9/16.
 */

public class SearchByFieldActivity extends BaseActivity {

    private EditText editTextName, editTextMobileNumber, editTextVehicleNumber, editTextFromPlace,
            editTextDestinationPlace, editTextInTime, editTextOutTime; //, editTextDatePicker;
    private static View timePicker;//, datePicker;
    private static String dateString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_by_field_layout);
        renderSearchLayout();
    }

    private void renderSearchLayout() {
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextMobileNumber = (EditText) findViewById(R.id.editTextMobileNumber);
        editTextVehicleNumber = (EditText) findViewById(R.id.editTextVehicleNumber);
        editTextFromPlace = (EditText) findViewById(R.id.editTextFromPlace);
        editTextDestinationPlace = (EditText) findViewById(R.id.editTextDestinationPlace);
        editTextInTime = (EditText) findViewById(R.id.editTextInTime);
        editTextOutTime = (EditText) findViewById(R.id.editTextOutTime);
        //editTextDatePicker = (EditText) findViewById(R.id.editTextDatePicker);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_date, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_date:
                showDatePickerDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new SearchByFieldActivity.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onInTimeClick(View view) {
        this.timePicker = view;
        showTimePickerDialog(view);
    }

    public void onOutTimeClick(View view) {
        this.timePicker = view;
        showTimePickerDialog(view);
    }

//    public void onDatePickerClick(View view) {
//        this.datePicker = view;
//        DialogFragment newFragment = new DatePickerFragment();
//        newFragment.show(getSupportFragmentManager(), "datePicker");
//    }

    private void showTimePickerDialog(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            EditText editTextTimePicker = (EditText) timePicker;
            editTextTimePicker.setText(hourOfDay + ":" + minute);
        }
    }


    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // Do something with the time chosen by the user
            //EditText editTextTimePicker = (EditText) datePicker;
            //editTextTimePicker.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
            dateString = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
        }
    }

    private boolean isEditTextEmpty(EditText editText) {
        return editText.getText() == null || TextUtils.isEmpty(editText.getText().toString().trim());
    }

    public void onSearchBtnClicked(View view){
        hideKeyboard(this, editTextName);

        // Sending request
        /*
        final HashMap<String, String> payload = new HashMap<>();
        payload.put(Constants.NAME, !isEditTextEmpty(editTextName) ? editTextName.getText().toString().trim() : null);
        payload.put(Constants.MOBILE_NO, !isEditTextEmpty(editTextMobileNumber) ? editTextMobileNumber.getText().toString().trim() : null);
        payload.put(Constants.FROM_PLACE, !isEditTextEmpty(editTextFromPlace) ? editTextFromPlace.getText().toString().trim() : null);
        payload.put(Constants.DESTINATION_PLACE, !isEditTextEmpty(editTextDestinationPlace) ? editTextDestinationPlace.getText().toString() : null);
        */


        /*
        payload.put(Constants.IN_TIME, !isEditTextEmpty(editTextInTime) ? date + editTextInTime.getText().toString().trim() + ":00" : null);
        payload.put(Constants.OUT_TIME, !isEditTextEmpty(editTextOutTime) ? date + editTextOutTime.getText().toString().trim() + ":00" : null);
        payload.put(Constants.VEHICLE_NO, !isEditTextEmpty(editTextVehicleNumber) ? editTextVehicleNumber.getText().toString().trim() : null);
        */

        String date;
        if (UIUtil.isEmpty(dateString)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            date = dateFormat.format(new Date());
        } else {
            date = dateString;
        }

        searchVisitor(!isEditTextEmpty(editTextName) ? editTextName.getText().toString().trim() : null,
                !isEditTextEmpty(editTextMobileNumber) ? editTextMobileNumber.getText().toString().trim() : null,
                !isEditTextEmpty(editTextFromPlace) ? editTextFromPlace.getText().toString().trim() : null,
                !isEditTextEmpty(editTextDestinationPlace) ? editTextDestinationPlace.getText().toString() : null,
                !isEditTextEmpty(editTextInTime) ? date + " " + editTextInTime.getText().toString().trim() + ":00" : null,
                !isEditTextEmpty(editTextOutTime) ? date + " " + editTextOutTime.getText().toString().trim() + ":00" : null,
                !isEditTextEmpty(editTextVehicleNumber) ? editTextVehicleNumber.getText().toString().trim() : null);
    }

    public void searchVisitor(String name, String mobileNumber, String fromPlace, String destPlace,
                              String inTime, String outTime, String vehicleNumber) {

        if (UIUtil.isEmpty(name) && UIUtil.isEmpty(mobileNumber) && UIUtil.isEmpty(fromPlace) && UIUtil.isEmpty(destPlace)
                && UIUtil.isEmpty(inTime) && UIUtil.isEmpty(outTime) && UIUtil.isEmpty(vehicleNumber)) {
            showToast(getString(R.string.fill_at_least_one_field));
            return;
        }

        if (!checkInternetConnection()) {
            getHandler().sendOfflineError(true);
            return;
        }

        showProgressDialog(getString(R.string.please_wait), true);
        ApiService apiService = ApiAdapter.getApiService(this);
        Call<ApiResponse<ArrayList<VisitorModel>>> call = apiService.getSearchByField(name, mobileNumber, fromPlace, destPlace,
                inTime, outTime, vehicleNumber);
        call.enqueue(new NetworkCallback<ApiResponse<ArrayList<VisitorModel>>>(this) {
            @Override
            public void onSuccess(ApiResponse<ArrayList<VisitorModel>> searchResult) {
                hideProgressDialog();
                if (searchResult.status == 0) {
                    ArrayList<VisitorModel> visitorModelArrayList = searchResult.apiResponseContent;
                    Intent visitorListingIntent = new Intent(SearchByFieldActivity.this, VisitorListingActivity.class);
                    visitorListingIntent.putParcelableArrayListExtra(Constants.VISITORS, visitorModelArrayList);
                    //visitorListingIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(visitorListingIntent);
                    finish();
                } else {
                    handler.sendEmptyMessage(searchResult.status, searchResult.message);
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


    @Override
    public String getScreenTag() {
        return getClass().getSimpleName();
    }
}
