package visitor.app.com.visitormanagement.database;

import android.database.Cursor;

/**
 * Created by jugal on 13/8/16.
 */
public class CreateVisitorObjHelper {

    /*
    private String wbId;
    private String name;
    private String mobileNumber;
    private String vehicleNumber;
    private String fromPlace;
    private String destiPlace;
    private String inTime;
    private String outTime;
    */

    private int Id;
    private String vPhotoUrl;
    private String vSignatureUrl;
    private String params;

    public static final String[] PROJECTION = new String[]{
            /*
            CreateVisitorHelper.WB_ID,
            CreateVisitorHelper.NAME,
            CreateVisitorHelper.MOBILE_NO,
            CreateVisitorHelper.VEHICLE_NO,
            CreateVisitorHelper.FROM_PLACE,
            CreateVisitorHelper.DESTINATION_PLACE,
            CreateVisitorHelper.IN_TIME,
            CreateVisitorHelper.OUT_TIME,
            */
            CreateVisitorHelper.ID,
            CreateVisitorHelper.V_PHOTO,
            CreateVisitorHelper.V_SIGNATURE_PHOTO,
    CreateVisitorHelper.PARAMS};

//    private static final int COLUMN_QUERY_INDEX = 0;
//    private static final int COLUMN_NAME_INDEX = 1;
//    private static final int COLUMN_MOBILE_NO_INDEX = 2;
//    private static final int COLUMN_VEHICLE_NO_INDEX = 3;
//    private static final int COLUMN_FROM_PLACE_INDEX = 4;
//    private static final int COLUMN_DEST_PLACE_INDEX = 5;
//    private static final int COLUMN_IN_TIME_INDEX = 6;
//    private static final int COLUMN_OUT_TIME_INDEX = 7;
private static final int COLUMN_ID_INDEX = 0;
    private static final int COLUMN_V_PHOTO_INDEX = 1;
    private static final int COLUMN_V_SIGN_PHOTO_INDEX = 2;
    private static final int COLUMN_PARAMS_INDEX = 3;



    public CreateVisitorObjHelper(Cursor cursor) {
//        this.wbId = cursor.getString(COLUMN_QUERY_INDEX);
//        this.name = cursor.getString(COLUMN_NAME_INDEX);
//        this.mobileNumber = cursor.getString(COLUMN_MOBILE_NO_INDEX);
//        this.vehicleNumber = cursor.getString(COLUMN_VEHICLE_NO_INDEX);
//        this.fromPlace = cursor.getString(COLUMN_FROM_PLACE_INDEX);
//        this.destiPlace = cursor.getString(COLUMN_DEST_PLACE_INDEX);
//        this.inTime = cursor.getString(COLUMN_IN_TIME_INDEX);
//        this.outTime = cursor.getString(COLUMN_OUT_TIME_INDEX);
        this.Id = cursor.getInt(COLUMN_ID_INDEX);
        this.vPhotoUrl = cursor.getString(COLUMN_V_PHOTO_INDEX);
        this.vSignatureUrl = cursor.getString(COLUMN_V_SIGN_PHOTO_INDEX);
        this.params = cursor.getString(COLUMN_PARAMS_INDEX);
    }

    /*
    public String getWbId() {
        return wbId;
    }

    public String getName() {
        return name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getFromPlace() {
        return fromPlace;
    }

    public String getDestiPlace() {
        return destiPlace;
    }

    public String getInTime() {
        return inTime;
    }

    public String getOutTime() {
        return outTime;
    }
    */

    public String getvPhotoUrl() {
        return vPhotoUrl;
    }

    public String getvSignatureUrl() {
        return vSignatureUrl;
    }

    public String getParams() {
        return params;
    }

    public int getId() {
        return Id;
    }
}
