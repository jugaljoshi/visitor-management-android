package visitor.app.com.visitormanagement.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import visitor.app.com.visitormanagement.application.BaseApplication;

/**
 * Created by jugal on 13/8/16.
 */
public class DatabaseContentProvider extends ContentProvider {
    private DatabaseHelper databaseHelper;
    private static final String TAG = DatabaseContentProvider.class.getName();

    public static final String AUTHORITY = "visitor.app.com.visitormanagement.provider";

    public static final Uri CONTENT_URI_PREFIX = Uri.parse("content://" + AUTHORITY);

    public static final int VISITOR_URI_DATA_DIR = 101;
    public static final int VISITOR_URI_DATA_ITEM = 102;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, CreateVisitorHelper.TABLE_NAME + "/", VISITOR_URI_DATA_DIR);
        sURIMatcher.addURI(AUTHORITY, CreateVisitorHelper.TABLE_NAME + "/#", VISITOR_URI_DATA_ITEM);
    }

    public DatabaseContentProvider() {
    }

    private String updateIdSelection(@NonNull Uri uri, String selection, String[] selectionArgs){
        switch (sURIMatcher.match(uri)) {
            case VISITOR_URI_DATA_ITEM:
                String id = uri.getLastPathSegment();
                String idSelection = CreateVisitorHelper.ID + " = '" + id + "'";
                if (selection == null) {
                    selection = idSelection;
                } else {
                    selection += " AND " + idSelection;
                }
                break;
        }
        return selection;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
        String tableName = getTableName(uri);
        selection = updateIdSelection(uri, selection, selectionArgs);
        int rowCount;
        sqlDB.beginTransaction();
        try {
            rowCount = sqlDB.delete(tableName, selection, selectionArgs);
            sqlDB.setTransactionSuccessful();
        } finally {
            sqlDB.endTransaction();
        }
        notifyChange(uri);
        return rowCount;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case VISITOR_URI_DATA_DIR:
                return CreateVisitorHelper.MIME_TYPE_DIR;
            case VISITOR_URI_DATA_ITEM:
                return CreateVisitorHelper.MIME_TYPE_ITEM;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
        String tableName = getTableName(uri);
        long newID;
        sqlDB.beginTransaction();
        try {
            newID = sqlDB.insert(tableName, null, values);
            sqlDB.setTransactionSuccessful();
        } finally {
            sqlDB.endTransaction();
        }
        if (newID > 0) {
            Uri newUri = ContentUris.withAppendedId(uri, newID);
            notifyChange(uri);
            return newUri;
        } else {
            return null;
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        Log.d(TAG, "Running bulkInsert for uri = " + uri);
        int insertedCount = 0;
        SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
        String tableName = getTableName(uri);
        sqlDB.beginTransaction();
        try {
            for (ContentValues cv : values) {
                sqlDB.insertOrThrow(tableName, null, cv);
                insertedCount++;
            }
            sqlDB.setTransactionSuccessful();
            notifyChange(uri);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed bulkInsert for uri = " + uri, e);
        } finally {
            sqlDB.endTransaction();
        }
        return insertedCount;
    }

    @Override
    public boolean onCreate() {
        databaseHelper = DatabaseHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String tableName = getTableName(uri);
        queryBuilder.setTables(tableName);
        Log.d(TAG, "Running query for uri = " + uri);
        selection = updateIdSelection(uri, selection, selectionArgs);
        Cursor cursor = queryBuilder.query(databaseHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        String tableName = getTableName(uri);
        Log.d(TAG, "Running update for uri = " + uri);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        selection = updateIdSelection(uri, selection, selectionArgs);
        db.beginTransaction();
        int rowCount;
        try {
            rowCount = db.update(tableName, values, selection, selectionArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if (rowCount > 0) {
            notifyChange(uri);
        }
        return rowCount;
    }

    private String getTableName(Uri uri) throws IllegalArgumentException {
        switch (sURIMatcher.match(uri)) {
            case VISITOR_URI_DATA_DIR:
            case VISITOR_URI_DATA_ITEM:
                return CreateVisitorHelper.TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private void notifyChange(@NonNull Uri uri) {
        Context context = getContext();
        if (context == null) {
            context = BaseApplication.getContext();
        }
        context.getContentResolver().notifyChange(uri, null);
    }
}

