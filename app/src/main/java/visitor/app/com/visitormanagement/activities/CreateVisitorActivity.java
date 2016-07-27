package visitor.app.com.visitormanagement.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import visitor.app.com.visitormanagement.ImageUtil.ImageUtil;
import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.fragment.AbstractFragment;
import visitor.app.com.visitormanagement.interfaces.ApiService;
import visitor.app.com.visitormanagement.interfaces.ImageUtilAware;
import visitor.app.com.visitormanagement.interfaces.NavigationCodes;
import visitor.app.com.visitormanagement.models.ApiResponse;
import visitor.app.com.visitormanagement.models.VisitorModel;
import visitor.app.com.visitormanagement.utils.ApiAdapter;
import visitor.app.com.visitormanagement.utils.Constants;
import visitor.app.com.visitormanagement.utils.DialogButton;
import visitor.app.com.visitormanagement.utils.NetworkCallback;
import visitor.app.com.visitormanagement.utils.UIUtil;
import visitor.app.com.visitormanagement.view.SignatureView;

/**
 * Created by jugal on 18/7/16.
 */
public class CreateVisitorActivity extends BaseActivity implements ImageUtilAware {


    private static final String MIME_TYPE = "multipart/form-data";
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int REQUEST_CAMERA_DIALOG = 101;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 102;


    private static View timePicker;
    private EditText editTextName, editTextMobileNumber, editTextVehicleNumber, editTextFromPlace,
            editTextDestinationPlace, editTextInTime, editTextOutTime;
    private TextInputLayout textInputName, textInputMobileNumber, textVehicleNumber, textInputFromPlace,
            textInputDestinationPlace, textInputInTime, textInputOutTime;
    private ImageView visitorImageView, visitorSignImageView;
    private LinearLayout layoutVisitorForm, layoutSign;
    private String visitorImageFileName, signImageFileName;
    private String wbId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crete_visitor_signature_layout);
        wbId = getIntent().getStringExtra(Constants.WB_ID);
        if(UIUtil.isEmpty(wbId)) return;
        layoutVisitorForm = (LinearLayout) findViewById(R.id.layoutVisitorForm);
        layoutSign = (LinearLayout) findViewById(R.id.layoutSign);
        showVisitorForm();
    }

    /*
    public void addToMainLayout(AbstractFragment fragment, String tag, boolean stateLess) {
        if (fragment == null || fragment.isAdded()) return;
        FragmentManager fm = getSupportFragmentManager();
        String ftTag = TextUtils.isEmpty(tag) ? fragment.getFragmentTxnTag() : tag;
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.content_frame, fragment, ftTag);
        ft.addToBackStack(ftTag);
        if (stateLess) {
            ft.commitAllowingStateLoss();
        } else {
            ft.commit();
        }
    }
    */

    private void showVisitorForm() {

        layoutVisitorForm.removeAllViews();
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View createVisitorLayout = layoutInflater.inflate(R.layout.create_visitor, layoutVisitorForm, false);
        layoutVisitorForm.addView(createVisitorLayout);
        layoutVisitorForm.setVisibility(View.VISIBLE);

        editTextName = (EditText) createVisitorLayout.findViewById(R.id.editTextName);
        editTextMobileNumber = (EditText) createVisitorLayout.findViewById(R.id.editTextMobileNumber);
        editTextVehicleNumber = (EditText) createVisitorLayout.findViewById(R.id.editTextVehicleNumber);
        editTextFromPlace = (EditText) createVisitorLayout.findViewById(R.id.editTextFromPlace);
        editTextDestinationPlace = (EditText) createVisitorLayout.findViewById(R.id.editTextDestinationPlace);
        editTextInTime = (EditText) createVisitorLayout.findViewById(R.id.editTextInTime);
        editTextOutTime = (EditText) createVisitorLayout.findViewById(R.id.editTextOutTime);

        visitorImageView = (ImageView) createVisitorLayout.findViewById(R.id.visitorImageView);
        visitorSignImageView = (ImageView) createVisitorLayout.findViewById(R.id.visitorSignImageView);


        textInputName = (TextInputLayout) createVisitorLayout.findViewById(R.id.textInputName);
        textInputMobileNumber = (TextInputLayout) createVisitorLayout.findViewById(R.id.textInputMobileNumber);
        textVehicleNumber = (TextInputLayout) createVisitorLayout.findViewById(R.id.textVehicleNumber);
        textInputFromPlace = (TextInputLayout) createVisitorLayout.findViewById(R.id.textInputFromPlace);
        textInputDestinationPlace = (TextInputLayout) createVisitorLayout.findViewById(R.id.textInputDestinationPlace);

        textInputInTime = (TextInputLayout) createVisitorLayout.findViewById(R.id.textInputInTime);
        textInputOutTime = (TextInputLayout) createVisitorLayout.findViewById(R.id.textInputOutTime);

        //showReqFields();

        final View signatureLayout = layoutInflater.inflate(R.layout.signature_layout, layoutVisitorForm, false);
        layoutSign.addView(signatureLayout);
        layoutSign.setVisibility(View.GONE);

        final SignatureView signatureView  = (SignatureView) signatureLayout.findViewById(R.id.signatureView);
//        signatureView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT));

        Button btnSelectSignature = (Button) signatureLayout.findViewById(R.id.btnSelectSignature);
        btnSelectSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signatureView.signatureDrawn) {
                    String filePath = ImageUtil.storeBitMapToFile(signatureView.mBitmap, "signature");
                    if (!UIUtil.isEmpty(filePath)) {
                        SampleImageTask sampleImageTask = new SampleImageTask(filePath, visitorSignImageView, true);
                        sampleImageTask.execute();
                    } else {
                        showToast("Sorry! Failed to capture signature");
                    }
                } else {
                    showToast("Please sign on screen!");
                }
            }
        });

        Button btnClearSignature = (Button) signatureLayout.findViewById(R.id.btnClearSignature);
        btnClearSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signatureView != null)
                    signatureView.clear();
            }
        });
    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void onCaptureSignatureClicked(View view) {
        layoutSign.setVisibility(View.VISIBLE);
        layoutVisitorForm.setVisibility(View.GONE);

        //SignatureFragment signatureFragment = new SignatureFragment();
        //addToMainLayout(signatureFragment, null, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    fireCameraIntent();
                } else {
                    // Permission
                    showToast("Permission denied");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void onCaptureImageClicked(View view) {
        if (!isDeviceSupportCamera()) {
            showToast("Sorry! Your device doesn't support camera");
            return;
        }

        int hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showAlertDialog("Permission Error!", "You need to allow access to Camera", DialogButton.OK, DialogButton.NO,
                        REQUEST_CAMERA_DIALOG);
                return;
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        fireCameraIntent();
    }

    @Override
    protected void onPositiveButtonClicked(int sourceName, Bundle valuePassed) {
        if (sourceName == REQUEST_CAMERA_DIALOG) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            super.onPositiveButtonClicked(sourceName, valuePassed);
        }
    }

    private void fireCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setSuspended(false);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                String filePath = ImageUtil.storeBitMapToFile(bitmap, "visitor");
                if (!UIUtil.isEmpty(filePath)) {
                    SampleImageTask sampleImageTask = new SampleImageTask(filePath, visitorImageView, false);
                    sampleImageTask.execute();
                    //setPic(filePath);
                } else {
                    showToast("Sorry! Failed to capture image");
                }
            } else if (resultCode == RESULT_CANCELED) {
                showToast("User cancelled image capture");
                // user cancelled Image capture
            } else {
                // failed to capture image
                showToast("Sorry! Failed to capture image");
            }
        } else if (resultCode == NavigationCodes.RC_SINGNATURE) {
            Bitmap signatureBitMapData = data.getParcelableExtra(Constants.SIGN_IMAGE_BYTE_DATA);
            String filePath = ImageUtil.storeBitMapToFile(signatureBitMapData, "signature");
            if (!UIUtil.isEmpty(filePath)) {
                SampleImageTask sampleImageTask = new SampleImageTask(filePath, visitorSignImageView, true);
                sampleImageTask.execute();
                //setPic(filePath);
            } else {
                showToast("Sorry! Failed to capture signature");
            }
        }
    }

    private void setPic(String imagePath, ImageView imageView) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(ImageUtil.getOutputMediaFile(type));
    }

    public void onInTimeClick(View view) {
        this.timePicker = view;
        showTimePickerDialog(view);
    }

    public void onOutTimeClick(View view) {
        this.timePicker = view;
        showTimePickerDialog(view);
    }

    private boolean isEditTextEmpty(EditText editText) {
        return editText.getText() == null || TextUtils.isEmpty(editText.getText().toString().trim());
    }

    public void onSaveButtonClick(View view) {
        UIUtil.resetFormInputField(textInputName);
        UIUtil.resetFormInputField(textInputMobileNumber);
        UIUtil.resetFormInputField(textVehicleNumber);
        UIUtil.resetFormInputField(textInputFromPlace);
        UIUtil.resetFormInputField(textInputDestinationPlace);
        UIUtil.resetFormInputField(textInputInTime);
        UIUtil.resetFormInputField(textInputOutTime);

        hideKeyboard(this, editTextName);

        if(UIUtil.isEmpty(wbId)){
            showToast(getString(R.string.client_error));
            return;
        }

        // Validation
        boolean cancel = false;
        View focusView = null;
        if (isEditTextEmpty(editTextName)) {
            UIUtil.reportFormInputFieldError(textInputName, getString(R.string.error_field_required));
            focusView = editTextName;
            cancel = true;
        }
        if (!UIUtil.isAlphaString(editTextName.getText().toString().trim())) {
            cancel = true;
            if (focusView == null) focusView = editTextName;
            UIUtil.reportFormInputFieldError(textInputName, getString(R.string.error_field_name));
        }
        if (isEditTextEmpty(editTextMobileNumber)) {
            UIUtil.reportFormInputFieldError(textInputMobileNumber, getString(R.string.error_field_required));
            if (focusView == null)
                focusView = editTextMobileNumber;
            cancel = true;
        } else if (editTextMobileNumber.getText().toString().length() < 10) {
            UIUtil.reportFormInputFieldError(textInputMobileNumber, getString(R.string.contactNoMin10));
            if (focusView == null)
                focusView = editTextMobileNumber;
            cancel = true;
        }
        if (isEditTextEmpty(editTextFromPlace)) {
            UIUtil.reportFormInputFieldError(textInputFromPlace, getString(R.string.error_field_required));
            if (focusView == null)
                focusView = editTextFromPlace;
            cancel = true;
        }

        if (isEditTextEmpty(editTextDestinationPlace)) {
            UIUtil.reportFormInputFieldError(textInputDestinationPlace, getString(R.string.error_field_required));
            if (focusView == null)
                focusView = editTextDestinationPlace;
            cancel = true;
        }

        if (isEditTextEmpty(editTextInTime)) {
            UIUtil.reportFormInputFieldError(textInputInTime, getString(R.string.error_field_required));
            if (focusView == null)
                focusView = editTextInTime;
            cancel = true;
        }

        if (isEditTextEmpty(editTextOutTime)) {
            UIUtil.reportFormInputFieldError(textInputOutTime, getString(R.string.error_field_required));
            if (focusView == null)
                focusView = editTextOutTime;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return;
        }

        if(UIUtil.isEmpty(visitorImageFileName) || UIUtil.isEmpty(signImageFileName)){
            showToast(getString(R.string.v_image_n_sign_mandatory));
            return;
        }

        // Sending request
        final HashMap<String, String> payload = new HashMap<>();
        payload.put(Constants.WB_ID, wbId);
        payload.put(Constants.NAME, editTextName.getText().toString());
        payload.put(Constants.MOBILE_NO, editTextMobileNumber.getText().toString());
        payload.put(Constants.FROM_PLACE, editTextFromPlace.getText().toString());
        payload.put(Constants.DESTINATION_PLACE, editTextDestinationPlace.getText().toString());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd ", Locale.getDefault());
        String date = dateFormat.format(new Date());
        payload.put(Constants.IN_TIME, date+editTextInTime.getText().toString()+":00");
        payload.put(Constants.OUT_TIME, date+editTextOutTime.getText().toString()+":00");
        payload.put(Constants.VEHICLE_NO, editTextVehicleNumber.getText().toString());

        createVisitor(new File(visitorImageFileName), new File(signImageFileName), payload);
    }

    public void createVisitor(File visitorImage, File visitorSign, HashMap<String, String> payload) {
        if (!checkInternetConnection()) {
            getHandler().sendOfflineError(true);
            return;
        }
        showProgressDialog(getString(R.string.please_wait), true);

        RequestBody visitorImageRequestFile = RequestBody.create(MediaType.parse(MIME_TYPE), visitorImage);
        MultipartBody.Part visitorImageBody = MultipartBody.Part.createFormData(Constants.VISITOR_IMAGE_FILE, visitorImage.getName(), visitorImageRequestFile);

        RequestBody visitorSignRequestFile = RequestBody.create(MediaType.parse(MIME_TYPE), visitorSign);
        MultipartBody.Part visitorSignBody = MultipartBody.Part.createFormData(Constants.SIGN_IMAGE_FILE, visitorSign.getName(), visitorSignRequestFile);

        ApiService apiService = ApiAdapter.getApiService(this);
        Call<ApiResponse> call = apiService.postVisitor(visitorImageBody, visitorSignBody, payload);
        call.enqueue(new NetworkCallback<ApiResponse>(this) {
            @Override
            public void onSuccess(ApiResponse createVisitorResponse) {
                hideProgressDialog();
                if (createVisitorResponse.status == 0) {
                    showToast("Visitor entry successfully created!");
                    Intent intent = new Intent();
                    intent.putExtra(Constants.WB_ID, wbId);
                    setResult(NavigationCodes.RC_CREATED_VISITOR, intent);
                    finish();
                } else {
                    handler.sendEmptyMessage(createVisitorResponse.status, createVisitorResponse.message);
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

        /*
        //todo sample images
        ApiService apiService = ApiAdapter.getApiService(this);
        // finally, execute the request
        Response response = null;
        Call<ApiResponse> call = apiService.postVisitor(visitorImageBody, visitorSignBody, payload); //payload
        try {
            response = call.execute();
        } catch (Throwable e) {
        }
        if (response == null || isSuspended()) return;

        if (response.isSuccessful()) {
            ApiResponse apiResponse = (ApiResponse) response.body();
            switch (apiResponse.status) {
                case 0:
                    showToast("Visitor entry successfully created!");
                    break;
                default:
                    handler.sendEmptyMessage(apiResponse.status,
                            apiResponse.message, true);
                    break;
            }
        } else {
            handler.handleHttpError(response.code(), response.message(), false);
        }
        */
    }

    private void showTimePickerDialog(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void showReqFields() {
        UIUtil.reportFormInputFieldError(textInputName, getString(R.string.error_field_name));
        UIUtil.reportFormInputFieldError(textInputMobileNumber, getString(R.string.error_field_required));
        UIUtil.reportFormInputFieldError(textInputFromPlace, getString(R.string.error_field_required));
        UIUtil.reportFormInputFieldError(textInputDestinationPlace, getString(R.string.error_field_required));
        UIUtil.reportFormInputFieldError(textInputInTime, getString(R.string.error_field_required));
        UIUtil.reportFormInputFieldError(textInputOutTime, getString(R.string.error_field_required));

    }

    private void sampleImage(String filePath, ImageView imageView, boolean isSignatureImage) {
        try {
            Bitmap bmpPic = ImageUtil.getBitmap(filePath, this);
            int compressQuality = 100; //PNG is a lossless format
            FileOutputStream fos = new FileOutputStream(filePath);
            if (bmpPic != null) {
                bmpPic.compress(Bitmap.CompressFormat.PNG, compressQuality, fos);
                fos.flush();
                fos.close();
                if (isSignatureImage){
                    signImageFileName = filePath;
                }else {
                    visitorImageFileName = filePath;
                }
            }
            imageView.setImageBitmap(bmpPic);
        } catch (Exception e) {

        }
    }

    @Override
    public String getScreenTag() {
        return getClass().getSimpleName();
    }

    @Override
    public void showErrorDialog(String message) {
        if (!isSuspended()) showAlertDialog(message);
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

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            EditText editTextTimePicker = (EditText) timePicker;
            editTextTimePicker.setText(hourOfDay + ":" + minute);
        }
    }

    public class SampleImageTask extends AsyncTask<String, Void, Void> {

        private String path;
        private ImageView imageView;
        private boolean isSignatureImage;

        public SampleImageTask(String path, ImageView imageView, boolean isSignatureImage) {
            this.path = path;
            this.imageView = imageView;
            this.isSignatureImage = isSignatureImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isSuspended()) {
                cancel(true);
            } else {
                showProgressDialog(getString(R.string.please_wait), false);
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!isSuspended()) {
                hideProgressDialog();
            } else {
                return;
            }
            layoutVisitorForm.setVisibility(View.VISIBLE);
            layoutSign.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... params) {
            if (isCancelled()) {
                return null;
            }
            sampleImage(path, imageView, isSignatureImage);

            return null;
        }

    }


    /*
    @Multipart
    @POST("/upload")
    void upload(@Part("myfile") TypedFile file,
                @Part("description") String description,
                Callback<String> cb);



    TypedFile typedFile = new TypedFile("multipart/form-data", new File("path/to/your/file"));
String description = "hello, this is description speaking";

service.upload(typedFile, description, new Callback<String>() {
    @Override
    public void success(String s, Response response) {
        Log.e("Upload", "success");
    }

    @Override
    public void failure(RetrofitError error) {
        Log.e("Upload", "error");
    }
});
     */
}
