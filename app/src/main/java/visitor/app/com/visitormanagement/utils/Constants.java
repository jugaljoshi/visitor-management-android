package visitor.app.com.visitormanagement.utils;

/**
 * Created by jugal on 16/7/16.
 */
public class Constants {
    public static final boolean SINK_DATA = false;
    public static final String USER_NAME = "email";
    public static final String PASSWORD = "password";
    public static final String RESPONSE = "response";
    public static final String AUTH_TOKEN = "auth_token";



    // extra
    public static final String FINISH_ACTIVITY = "__bb_finish_activity__"; //special boolean dialog argument
    public static final String ACTIVITY_RESULT_CODE = "__bb_activity_result_code__";
    public static final String REDIRECT_INTENT = "redirect_intent";
    public static final String KEY_PERMISSION = "key_permission";
    public static final String KEY_PERMISSION_RC = "key_permission_rc";
    public static final String FRAGMENT_CODE = "fragmentCode";
    public static final String FRAGMENT_TAG = "fragmentTag";
    public static final String FRAGMENT_STATE = "fragmentState";
    public static final String EXTRA_CREATE_WORKBOOK = "create_work_book";



    //workbook
    public static final String WB_IMG_URL = "wb_img_url";
    public static final String WB_NAME = "wb_name";
    public static final String WB_TYPE = "wb_type";
    public static final String WB_TYPE_ID = "wb_type_id";
    public static final String WB_ID = "wb_id";
    public static final String VISITOR_MANDATORY_FIELDS = "mandatory_fields";
    public static final String WB_TYPES = "wb_types";
    public static final String VISITOR_INTENT = "visitor_intent";
    public static final String PARAMS = "params";

    /*
       @method_decorator(mapi_mandatory_parameters('name', 'mobile_no', 'from_place', 'destination_place',
                                               'in_time', 'out_time', 'signature', 'wb_type'))vehicle_no
    */
    //visitor
    public static final String NAME = "name";
    public static final String MOBILE_NO = "mobile_no";
    public static final String VEHICLE_NO = "vehicle_no";
    public static final String FROM_PLACE = "from_place";
    public static final String DESTINATION_PLACE = "destination_place";
    public static final String IN_TIME = "in_time";
    public static final String OUT_TIME = "out_time";
    public static final String V_PHOTO = "photo";
    public static final String V_SIGNATURE_PHOTO = "signature";

    public static final String VISITOR_IMAGE_FILE = "photo";
    public static final String SIGN_IMAGE_FILE = "signature";
    public static final String SIGN_IMAGE_BYTE_DATA = "visitorSignByteData";
}
