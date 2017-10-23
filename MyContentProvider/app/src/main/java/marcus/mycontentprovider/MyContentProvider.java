package marcus.mycontentprovider;

//Marcus Rieker 10.23.2017
//Used some help from Jim Ward's lectures and examples found here: https://github.com/JimSeker/Android-Examples

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;
import marcus.mycontentprovider.db.mydb;
import marcus.mycontentprovider.db.mySQLiteHelper;

public class MyContentProvider extends ContentProvider {
    private static final int ACCOUNTS = 200;
    private static final int ACCOUNTS_ID = 210;
    private static final int CATEGORY = 300;
    private static final int CATEGORY_ID = 310;
    public static final String PROVIDER_NAME = "edu.cs4730.prog4db";
    private static final int TRANSACTIONS = 100;
    private static final int TRANSACTIONS_ID = 110;
    private static final UriMatcher uriMatcher = new UriMatcher(-1);
    mydb db;

    static {
        uriMatcher.addURI(PROVIDER_NAME, mySQLiteHelper.KEY_CAT, CATEGORY);
        uriMatcher.addURI(PROVIDER_NAME, "Category/#", CATEGORY_ID);
        uriMatcher.addURI(PROVIDER_NAME, "Accounts", ACCOUNTS);
        uriMatcher.addURI(PROVIDER_NAME, "Accounts/#", ACCOUNTS_ID);
        uriMatcher.addURI(PROVIDER_NAME, "Accounts/transactions/#", 100);
        uriMatcher.addURI(PROVIDER_NAME, "Accounts/transactions/#/#", 110);
    }

    public boolean onCreate() {
        this.db = new mydb(getContext());
        this.db.open();
        return true;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String Tablename;
        Cursor mCursor;
        switch (uriMatcher.match(uri)) {
            case 100:
                Tablename = this.db.getAccountname(Integer.valueOf(uri.getLastPathSegment()).intValue());
                break;
            case 110:
                Tablename = this.db.getAccountname(Integer.valueOf((String) uri.getPathSegments().get(2)).intValue());
                selection = fixit(selection, "_id = " + uri.getLastPathSegment());
                break;
            case ACCOUNTS /*200*/:
                Tablename = mySQLiteHelper.TABLE_ACCOUNTS;
                mCursor = this.db.db.rawQuery("select * from accounts", null);
                if (mCursor != null) {
                    mCursor.moveToFirst();
                    mCursor.moveToFirst();
                    while (!mCursor.isAfterLast()) {
                        this.db.db.execSQL("DROP TABLE IF EXISTS " + mCursor.getString(mCursor.getColumnIndex(mySQLiteHelper.KEY_NAME)));
                        mCursor.moveToNext();
                    }
                    break;
                }
                break;
            case ACCOUNTS_ID /*210*/:
                Tablename = mySQLiteHelper.TABLE_ACCOUNTS;
                mCursor = this.db.db.rawQuery("select * from accounts where _id = " + uri.getLastPathSegment(), null);
                if (mCursor != null) {
                    mCursor.moveToFirst();
                    Log.v("Delete Account", "deleting table " + mCursor.getString(mCursor.getColumnIndex(mySQLiteHelper.KEY_NAME)));
                    this.db.db.execSQL("DROP TABLE IF EXISTS " + mCursor.getString(mCursor.getColumnIndex(mySQLiteHelper.KEY_NAME)));
                }
                selection = fixit(selection, "_id = " + uri.getLastPathSegment());
                break;
            case CATEGORY /*300*/:
                Tablename = mySQLiteHelper.TABLE_CAT;
                break;
            case CATEGORY_ID /*310*/:
                Tablename = mySQLiteHelper.TABLE_CAT;
                selection = fixit(selection, "_id = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        int count = this.db.cpDelete(Tablename, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case 100:
            case ACCOUNTS /*200*/:
            case CATEGORY /*300*/:
                return "vnd.android.cursor.dir/vnd.cs4730.data";
            case 110:
            case ACCOUNTS_ID /*210*/:
            case CATEGORY_ID /*310*/:
                return "vnd.android.cursor.item/vnd.cs4730.data";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    public Uri insert(Uri uri, ContentValues values) {
        if (values == null) {
            throw new IllegalArgumentException("Illegal Data " + uri);
        }
        String Tablename;
        switch (uriMatcher.match(uri)) {
            case 100:
                Tablename = this.db.getAccountname(Integer.valueOf(uri.getLastPathSegment()).intValue());
                break;
            case 110:
            case ACCOUNTS_ID /*210*/:
            case CATEGORY_ID /*310*/:
                throw new IllegalArgumentException("Unknown URI " + uri);
            case ACCOUNTS /*200*/:
                Tablename = mySQLiteHelper.TABLE_ACCOUNTS;
                this.db.createAccount(values.getAsString(mySQLiteHelper.KEY_NAME));
                break;
            case CATEGORY /*300*/:
                Tablename = mySQLiteHelper.TABLE_CAT;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        long rowId = this.db.cpInsert(Tablename, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c;
        switch (uriMatcher.match(uri)) {
            case 100:
                c = this.db.cpQueryJoin(this.db.getAccountname(Integer.valueOf(uri.getLastPathSegment()).intValue()), projection, selection, selectionArgs, sortOrder);
                break;
            case 110:
                String Tablename = this.db.getAccountname(Integer.valueOf((String) uri.getPathSegments().get(2)).intValue());
                c = this.db.cpQueryJoin(Tablename, projection, fixit(selection, Tablename + "._id = " + uri.getLastPathSegment()), selectionArgs, sortOrder);
                break;
            case ACCOUNTS /*200*/:
                c = this.db.cpQuery(mySQLiteHelper.TABLE_ACCOUNTS, projection, selection, selectionArgs, sortOrder);
                break;
            case ACCOUNTS_ID /*210*/:
                c = this.db.cpQuery(mySQLiteHelper.TABLE_ACCOUNTS, projection, fixit(selection, "_id = " + uri.getLastPathSegment()), selectionArgs, sortOrder);
                break;
            case CATEGORY /*300*/:
                c = this.db.cpQuery(mySQLiteHelper.TABLE_CAT, projection, selection, selectionArgs, sortOrder);
                break;
            case CATEGORY_ID /*310*/:
                c = this.db.cpQuery(mySQLiteHelper.TABLE_CAT, projection, fixit(selection, "_id = " + uri.getLastPathSegment()), selectionArgs, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String Tablename;
        switch (uriMatcher.match(uri)) {
            case 100:
                Tablename = this.db.getAccountname(Integer.valueOf(uri.getLastPathSegment()).intValue());
                break;
            case 110:
                Tablename = this.db.getAccountname(Integer.valueOf((String) uri.getPathSegments().get(2)).intValue());
                selection = fixit(selection, "_id = " + uri.getLastPathSegment());
                break;
            case ACCOUNTS /*200*/:
                throw new IllegalArgumentException("Illegal URI " + uri);
            case ACCOUNTS_ID /*210*/:
                throw new IllegalArgumentException("Illegal URI " + uri);
            case CATEGORY /*300*/:
                Tablename = mySQLiteHelper.TABLE_CAT;
                break;
            case CATEGORY_ID /*310*/:
                Tablename = mySQLiteHelper.TABLE_CAT;
                selection = fixit(selection, "_id = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        int count = this.db.cpUpdate(Tablename, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private String fixit(String tofix, String item) {
        if (tofix == null) {
            return item;
        }
        if (tofix.compareTo("") == 0) {
            return item;
        }
        return tofix + " AND " + item;
    }
}
