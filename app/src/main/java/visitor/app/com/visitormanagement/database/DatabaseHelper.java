package visitor.app.com.visitormanagement.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jugal on 12/8/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "visitormanagement.db";

    protected static final int DATABASE_VERSION = 1;
    private static final Object lock = new Object();
    private static volatile DatabaseHelper dbAdapter = null;

    private DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static DatabaseHelper getInstance(Context context) {
        DatabaseHelper helper = dbAdapter;
        if (helper == null) {
            synchronized (lock) {
                helper = dbAdapter;
                if (helper == null) {
                    helper = new DatabaseHelper(context.getApplicationContext(),
                            DATABASE_NAME, null, DATABASE_VERSION);
                    dbAdapter = helper;
                }
            }
        }
        return dbAdapter;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        initializeDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private void initializeDatabase(SQLiteDatabase db) {
        db.execSQL(CreateVisitorHelper.CREATE_TABLE);
        db.execSQL(WorkbookHelper.CREATE_TABLE);
    }

}

