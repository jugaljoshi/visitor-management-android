package visitor.app.com.visitormanagement.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import visitor.app.com.visitormanagement.models.ApiResponse;
import visitor.app.com.visitormanagement.models.AutoSearchResponse;
import visitor.app.com.visitormanagement.models.LoginApiResponse;
import visitor.app.com.visitormanagement.models.VisitorModel;
import visitor.app.com.visitormanagement.models.WorkBookModel;
import visitor.app.com.visitormanagement.models.WorkBookResponse;
import visitor.app.com.visitormanagement.utils.Constants;

/**
 * Created by jugal on 16/7/16.
 */
public interface ApiService {

    @FormUrlEncoded
    @POST("login/")
    Call<ApiResponse<LoginApiResponse>> login(@Field(Constants.USER_NAME) String userName,
                                              @Field(Constants.PASSWORD) String password);

    @GET("get-workbook/")
    Call<ApiResponse<ArrayList<WorkBookModel>>> getHomePageData();

    @GET("get-workbook-type/")
    Call<ApiResponse<WorkBookResponse>> getWorkBookTypes();

    @FormUrlEncoded
    @POST("create-workbook/")
    Call<ApiResponse<ArrayList<WorkBookModel>>> getCreateWorkbook(@Field(Constants.WB_NAME) String wbName,
                                                                  @Field(Constants.WB_TYPE_ID) String wbTypeId,
                                                                  @Field(Constants.VISITOR_MANDATORY_FIELDS) String mandatoryFields);






    @GET("get-visitors/")
    Call<ApiResponse<ArrayList<VisitorModel>>> getVisitors(@Query(Constants.WB_ID) String workBookType,
                                                           @Query(Constants.NAME) String name);


    @Multipart
    //@Headers({"Content-Type: multipart/form-data"})
    //@FormUrlEncoded
    @POST("create-visitor/")
    Call<ApiResponse> postVisitor(@Part MultipartBody.Part visitorImageBody,
                                  @Part MultipartBody.Part visitorSignBody,
                                  @Part("params") HashMap<String, String> params); //@FieldMap HashMap<String, String> params


    @GET("search-tc/")
    Call<ApiResponse<ArrayList<AutoSearchResponse>>> getSearchTerms(@Query(Constants.NAME) String name);
}
