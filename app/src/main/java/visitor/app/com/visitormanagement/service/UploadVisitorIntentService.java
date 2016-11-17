package visitor.app.com.visitormanagement.service;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import visitor.app.com.visitormanagement.database.CreateVisitorHelper;
import visitor.app.com.visitormanagement.database.CreateVisitorObjHelper;
import visitor.app.com.visitormanagement.database.WorkbookHelper;
import visitor.app.com.visitormanagement.interfaces.ApiService;
import visitor.app.com.visitormanagement.models.ApiResponse;
import visitor.app.com.visitormanagement.models.WorkBookModel;
import visitor.app.com.visitormanagement.utils.ApiAdapter;
import visitor.app.com.visitormanagement.utils.Constants;
import visitor.app.com.visitormanagement.utils.DataUtil;
import visitor.app.com.visitormanagement.utils.UIUtil;

/**
 * Created by jugal on 15/8/16.
 */
public class UploadVisitorIntentService extends IntentService{
    private static final String TAG = "UploadVisitorIntentService";
    private static final String MIME_TYPE = "multipart/form-data";

    public UploadVisitorIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        uploadWorkBookData();
    }

    private void uploadWorkBookData(){
        ArrayList<WorkBookModel> workBookModelArrayList = WorkbookHelper.getVisitorRecordsForUpdate(this, -1);
        if (workBookModelArrayList != null && !workBookModelArrayList.isEmpty() && DataUtil.isInternetAvailable(this)) {

            for (WorkBookModel workBookModel : workBookModelArrayList) {
                ApiService bigBasketApiService = ApiAdapter.getApiService(this);
                try {
                    Call<ApiResponse<ArrayList<WorkBookModel>>> call = bigBasketApiService.getCreateWorkbook(workBookModel.getWbName(),
                            workBookModel.getWbId(),
                            TextUtils.join(",", workBookModel.getVisitorMandatoryFields())); //workBookModel.getVisitorMandatoryFieldsString()
                    Response<ApiResponse<ArrayList<WorkBookModel>>> response = call.execute();
                    if (response.isSuccessful()) {
                        ApiResponse apiResponse = response.body();
                        switch (apiResponse.status) {
                            case 0:
                                WorkbookHelper.recordUploaded(this, String.valueOf(workBookModel.getId()));
                                //WorkbookHelper.deleteRecord(this, String.valueOf(workBookModel.getId()));
                                break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            uploadVisitorData();
        }
    }

    private void uploadVisitorData(){
        ArrayList<CreateVisitorObjHelper> visitorObjHelpers = CreateVisitorHelper.getVisitorRecordsNeedToUpload(this, -1);
        Gson gson = new GsonBuilder().create();
        Type hashMapType = new TypeToken<HashMap<String, String>>() {}.getType();

        if (visitorObjHelpers != null && visitorObjHelpers.size() > 0 && DataUtil.isInternetAvailable(this)) {

            for (CreateVisitorObjHelper visitorObjHelper : visitorObjHelpers) {

                MultipartBody.Part visitorImageBody = null, visitorSignBody = null;
                if (!UIUtil.isEmpty(visitorObjHelper.getvPhotoUrl())) {
                    File visitorImage = new File(visitorObjHelper.getvPhotoUrl());
                    RequestBody visitorImageRequestFile = RequestBody.create(MediaType.parse(MIME_TYPE), visitorImage);
                    visitorImageBody = MultipartBody.Part.createFormData(Constants.VISITOR_IMAGE_FILE, visitorImage.getName(), visitorImageRequestFile);

                }
                if (!UIUtil.isEmpty(visitorObjHelper.getvSignatureUrl())) {
                    File visitorSign = new File(visitorObjHelper.getvSignatureUrl());
                    RequestBody visitorSignRequestFile = RequestBody.create(MediaType.parse(MIME_TYPE), visitorSign);
                    visitorSignBody = MultipartBody.Part.createFormData(Constants.SIGN_IMAGE_FILE, visitorSign.getName(), visitorSignRequestFile);
                }

                HashMap<String, String> payload = gson.fromJson(visitorObjHelper.getParams(), hashMapType);
                ApiService bigBasketApiService = ApiAdapter.getApiService(this);
                try {
                    Call<ApiResponse> call = bigBasketApiService.postVisitor(visitorImageBody,
                            visitorSignBody, payload);
                    Response<ApiResponse> response = call.execute();
                    if (response.isSuccessful()) {
                        ApiResponse apiResponse = response.body();
                        switch (apiResponse.status) {
                            case 0:
                                CreateVisitorHelper.deleteRecord(this, String.valueOf(visitorObjHelper.getId()));
                                break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
