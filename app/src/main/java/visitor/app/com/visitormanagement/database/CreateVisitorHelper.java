package visitor.app.com.visitormanagement.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import java.util.ArrayList;

import visitor.app.com.visitormanagement.utils.Constants;

/**
 * Created by jugal on 12/8/16.
 */
public class CreateVisitorHelper {

    public static final String ID = "_id";
    /*
    public static final String WB_ID = Constants.WB_ID;
    public static final String NAME = Constants.NAME;
    public static final String MOBILE_NO = Constants.MOBILE_NO;
    public static final String VEHICLE_NO = Constants.VEHICLE_NO;

    public static final String FROM_PLACE = Constants.FROM_PLACE;
    public static final String DESTINATION_PLACE = Constants.DESTINATION_PLACE;
    public static final String IN_TIME = Constants.IN_TIME;
    public static final String OUT_TIME = Constants.OUT_TIME;

    public static final String CREATE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %1$s (" +
                    "%2$s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%3$s TEXT NOT NULL," +
                    " %4$s TEXT," +
                    " %5$s TEXT," +
                    " %6$s TEXT," +
                    " %7$s TEXT," +
                    " %8$s TEXT," +
                    " %9$s TEXT," +
                    " %10$s TEXT," +
                    " %11$s TEXT," +
                    " %12$s TEXT,",
            TABLE_NAME, ID,
            WB_ID,
            NAME,
            MOBILE_NO,
            VEHICLE_NO,
            FROM_PLACE,
            DESTINATION_PLACE,
            IN_TIME,
            OUT_TIME,
            V_PHOTO,
            V_SIGNATURE_PHOTO);

    */
    public static final String V_PHOTO = Constants.V_PHOTO;
    public static final String V_SIGNATURE_PHOTO = Constants.V_SIGNATURE_PHOTO;
    public static final String PARAMS = Constants.PARAMS;
    public static final String NEED_UPLOAD = "need_upload";

    public static final String TABLE_NAME = "visitor";

    public static final String CREATE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %1$s (" +
                    "%2$s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%3$s TEXT," +
                    " %4$s TEXT," +
                    " %5$s TEXT NOT NULL);",
            TABLE_NAME,
            ID,
            V_PHOTO,
            V_SIGNATURE_PHOTO,
            PARAMS);

    public static final Uri CONTENT_URI = Uri.withAppendedPath(
            DatabaseContentProvider.CONTENT_URI_PREFIX, TABLE_NAME);
    public static final String MIME_TYPE_DIR =
            "vnd.android.cursor.dir/visitor.app.com.visitormanagement.visitor";
    public static final String MIME_TYPE_ITEM =
            "vnd.android.cursor.item/visitor.app.com.visitormanagement.visitor";

    public static ArrayList<CreateVisitorObjHelper> getVisitorRecords(Context context, int limit) {
        Cursor cursor = null;
        ArrayList<CreateVisitorObjHelper> visitorRecords = null;
        try {
            String sortOrder;
            if (limit <= 0) {
                sortOrder = ID;
            } else {
                sortOrder = ID + " DESC LIMIT " + String.valueOf(limit);
            }
            cursor = context.getContentResolver().query(CONTENT_URI,
                    CreateVisitorObjHelper.PROJECTION,
                    null, null, sortOrder);
            if (cursor != null && cursor.moveToFirst()) {
                visitorRecords = new ArrayList<>(cursor.getCount());
                do {
                    visitorRecords.add(new CreateVisitorObjHelper(cursor));
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return visitorRecords;
    }

    public static ArrayList<CreateVisitorObjHelper> getVisitorRecordsNeedToUpload(Context context, int limit) {
        Cursor cursor = null;
        ArrayList<CreateVisitorObjHelper> visitorRecords = null;
        try {
            String sortOrder;
            if (limit <= 0) {
                sortOrder = ID;
            } else {
                sortOrder = ID + " DESC LIMIT " + String.valueOf(limit);
            }
            cursor = context.getContentResolver().query(CONTENT_URI,
                    CreateVisitorObjHelper.PROJECTION,
                    NEED_UPLOAD+"=?", new String[] {"1"}, sortOrder);
            if (cursor != null && cursor.moveToFirst()) {
                visitorRecords = new ArrayList<>(cursor.getCount());
                do {
                    visitorRecords.add(new CreateVisitorObjHelper(cursor));
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return visitorRecords;
    }

    public static void update(Context context, String vPhoto, String vSignPhoto,
                              String payload, int needUpload) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(V_PHOTO, vPhoto);
        contentValues.put(V_SIGNATURE_PHOTO, vSignPhoto);
        contentValues.put(PARAMS, payload);
        contentValues.put(NEED_UPLOAD, needUpload);

        ContentResolver cr = context.getContentResolver();
        int count = cr.update(CONTENT_URI, contentValues,
                ID + " = ?", new String[]{ID});
        if (count <= 0) {
            cr.insert(CONTENT_URI, contentValues);
        }
    }

    /*
    public static void update(Context context, String wbId, String name,
                              String mobileNumber,
                              String vehicleNumber,
                              String fromPlace,
                              String destiPlace,
                              String inTime,
                              String outTime,
                              String vPhoto,
                              String vSignPhoto) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WB_ID, wbId);
        contentValues.put(NAME, name);
        contentValues.put(MOBILE_NO, mobileNumber);
        contentValues.put(VEHICLE_NO, vehicleNumber);
        contentValues.put(FROM_PLACE, fromPlace);
        contentValues.put(DESTINATION_PLACE, destiPlace);
        contentValues.put(IN_TIME, inTime);
        contentValues.put(OUT_TIME, outTime);
        contentValues.put(V_PHOTO, vPhoto);
        contentValues.put(V_SIGNATURE_PHOTO, vSignPhoto);

        ContentResolver cr = context.getContentResolver();
        int count = cr.update(CONTENT_URI, contentValues,
                ID + " = ?", new String[]{ID});
        if (count <= 0) {
            cr.insert(CONTENT_URI, contentValues);
        }
    }
    */

    public static void deleteRecord(Context context, String id) {
        context.getContentResolver().delete(CONTENT_URI, ID + " = ?", new String[]{id});
    }

}
