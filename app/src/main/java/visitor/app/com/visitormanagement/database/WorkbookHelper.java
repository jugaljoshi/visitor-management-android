package visitor.app.com.visitormanagement.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import java.util.ArrayList;

import visitor.app.com.visitormanagement.models.WorkBookModel;
import visitor.app.com.visitormanagement.utils.Constants;

/**
 * Created by jugal on 26/10/16.
 */

public class WorkbookHelper {
    public static final String ID = "_id";
    public static final String WORK_BOOK_NAME = Constants.WB_NAME;
    public static final String WB_TYPE_ID = Constants.WB_TYPE_ID;
    public static final String VISITOR_MANDATORY_FIELDS = Constants.VISITOR_MANDATORY_FIELDS;
    public static final String NEED_UPLOAD = "need_upload";

    public static final String TABLE_NAME = "workbook";

    public static final String CREATE_TABLE = String.format("CREATE TABLE IF NOT EXISTS %1$s (" +
                    "%2$s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%3$s TEXT NOT NULL," +
                    " %4$s TEXT NOT NULL," +
                    " %5$s TEXT NOT NULL," +
                    " %6$s INTEGER NOT NULL);",
            TABLE_NAME,
            ID,
            WORK_BOOK_NAME,
            WB_TYPE_ID,
            VISITOR_MANDATORY_FIELDS,
            NEED_UPLOAD);
    public static final String[] PROJECTION = new String[]{
            ID, WORK_BOOK_NAME, WB_TYPE_ID, VISITOR_MANDATORY_FIELDS, NEED_UPLOAD
           };

    public static final Uri CONTENT_URI = Uri.withAppendedPath(
            DatabaseContentProvider.CONTENT_URI_PREFIX, TABLE_NAME);
    public static final String MIME_TYPE_DIR =
            "vnd.android.cursor.dir/visitor.app.com.visitormanagement.workbook";
    public static final String MIME_TYPE_ITEM =
            "vnd.android.cursor.item/visitor.app.com.visitormanagement.workbook";

    public static ArrayList<WorkBookModel> getVisitorRecords(Context context, int limit) {
        Cursor cursor = null;
        ArrayList<WorkBookModel> workBookModelArrayList = null;
        try {
            String sortOrder;
            if (limit <= 0) {
                sortOrder = ID;
            } else {
                sortOrder = ID + " DESC LIMIT " + String.valueOf(limit);
            }
            cursor = context.getContentResolver().query(CONTENT_URI,
                    PROJECTION,
                    null, null, sortOrder);
            if (cursor != null && cursor.moveToFirst()) {
                workBookModelArrayList = new ArrayList<>(cursor.getCount());
                do {
                    workBookModelArrayList.add(new WorkBookModel(cursor));
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return workBookModelArrayList;
    }

    public static ArrayList<WorkBookModel> getVisitorRecordsForUpdate(Context context, int limit) {
        Cursor cursor = null;
        ArrayList<WorkBookModel> workBookModelArrayList = null;
        try {
            String sortOrder;
            if (limit <= 0) {
                sortOrder = ID;
            } else {
                sortOrder = ID + " DESC LIMIT " + String.valueOf(limit);
            }
            cursor = context.getContentResolver().query(CONTENT_URI,
                    PROJECTION,
                    NEED_UPLOAD+"=?", new String[] {"1"}, sortOrder);
            if (cursor != null && cursor.moveToFirst()) {
                workBookModelArrayList = new ArrayList<>(cursor.getCount());
                do {
                    workBookModelArrayList.add(new WorkBookModel(cursor));
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return workBookModelArrayList;
    }


    public static void update(Context context, String workBookName, String workBookTypeId,
                              String visitorMandatoryFields, int needUpload) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WORK_BOOK_NAME, workBookName);
        contentValues.put(WB_TYPE_ID, workBookTypeId);
        contentValues.put(VISITOR_MANDATORY_FIELDS, visitorMandatoryFields);
        contentValues.put(NEED_UPLOAD, needUpload);

        ContentResolver cr = context.getContentResolver();
        int count = cr.update(CONTENT_URI, contentValues,
                ID + " = ?", new String[]{ID});
        if (count <= 0) {
            cr.insert(CONTENT_URI, contentValues);
        }
    }

    public static void recordUploaded(Context context, String id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NEED_UPLOAD, 0);
        ContentResolver cr = context.getContentResolver();
        cr.update(CONTENT_URI, contentValues, ID + " = ?", new String[]{id});
    }

    public static void deleteTableData(Context context) {
        context.getContentResolver().delete(CONTENT_URI, null, null);
    }
    public static void deleteRecord(Context context, String id) {
        context.getContentResolver().delete(CONTENT_URI, ID + " = ?", new String[]{id});
    }

}
