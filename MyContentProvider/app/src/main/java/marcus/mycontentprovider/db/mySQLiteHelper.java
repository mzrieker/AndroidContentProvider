package marcus.mycontentprovider.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class mySQLiteHelper extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_CAT = "CREATE TABLE category (_id integer PRIMARY KEY autoincrement,CatName TEXT );";
    private static final String CREATE_TABLE_CHECKING = "CREATE TABLE checking (_id integer PRIMARY KEY autoincrement,Date TEXT, CheckNum TEXT, Name TEXT, Amount REAL, Category INTEGER, FOREIGN KEY(Category) REFERENCES category(Category) );";
    public static final String CREATE_TABLE_GENERIC = " (_id integer PRIMARY KEY autoincrement,Date TEXT, CheckNum TEXT, Name TEXT, Amount REAL, Category INTEGER, FOREIGN KEY(Category) REFERENCES category(Category) );";
    private static final String CREATE_TABLE_LIST = "CREATE TABLE accounts (_id integer PRIMARY KEY autoincrement,Name TEXT );";
    private static final String DATABASE_NAME = "myCheckBook.db";
    private static final int DATABASE_VERSION = 8;
    public static final String KEY_AMOUNT = "Amount";
    public static final String KEY_CAT = "Category";
    public static final String KEY_CATNAME = "CatName";
    public static final String KEY_DATE = "Date";
    public static final String KEY_NAME = "Name";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TYPE = "CheckNum";
    public static final String TABLE_ACCOUNTS = "accounts";
    public static final String TABLE_CAT = "category";
    public static final String TABLE_CHECKING = "checking";
    private static final String TAG = "SQLitehelper";

    public mySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 8);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CAT);
        db.execSQL(CREATE_TABLE_LIST);
        db.execSQL(CREATE_TABLE_CHECKING);
        db.execSQL("\ninsert into accounts(Name) values (\"checking\");");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS category");
        Cursor mCursor = db.rawQuery("select * from accounts", null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                db.execSQL("DROP TABLE IF EXISTS " + mCursor.getString(mCursor.getColumnIndex(KEY_NAME)));
                mCursor.moveToNext();
            }
        }
        db.execSQL("DROP TABLE IF EXISTS accounts");
        onCreate(db);
    }
}
