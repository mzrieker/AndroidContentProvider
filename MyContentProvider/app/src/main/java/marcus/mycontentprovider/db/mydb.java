package marcus.mycontentprovider.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import java.util.Arrays;

public class mydb {
    private marcus.mycontentprovider.db.mySQLiteHelper DBHelper;
    public SQLiteDatabase db;

    public mydb(Context ctx) {
        this.DBHelper = new mySQLiteHelper(ctx);
    }

    public void open() throws SQLException {
        this.db = this.DBHelper.getWritableDatabase();
    }

    public boolean isOpen() throws SQLException {
        return this.db.isOpen();
    }

    public void close() {
        this.DBHelper.close();
        this.db.close();
    }

    public long cpInsert(String TableName, ContentValues values) {
        return this.db.insert(TableName, null, values);
    }

    public Cursor cpQuery(String TableName, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TableName);
        return qb.query(this.db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public Cursor cpQueryJoin(String TableName, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TableName + " INNER JOIN " + mySQLiteHelper.TABLE_CAT + " ON " + mySQLiteHelper.KEY_CAT + " = " + "category._id");
        if (projection != null) {
            projection = (String[]) append(projection, mySQLiteHelper.KEY_CATNAME);
        }
        return qb.query(this.db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public int cpUpdate(String TableName, ContentValues values, String selection, String[] selectionArgs) {
        return this.db.update(TableName, values, selection, selectionArgs);
    }

    public int cpDelete(String TableName, String selection, String[] selectionArgs) {
        return this.db.delete(TableName, selection, selectionArgs);
    }

    public void emptyName() {
        this.db.delete(mySQLiteHelper.TABLE_CHECKING, null, null);
    }

    static <T> T[] append(T[] arr, T element) {
        int N = arr.length;
        arr = Arrays.copyOf(arr, N + 1);
        arr[N] = element;
        return arr;
    }

    public String getAccountname(int id) {
        Cursor c = cpQuery(mySQLiteHelper.TABLE_ACCOUNTS, new String[]{mySQLiteHelper.KEY_NAME}, "_id= " + id + "", null, null);
        if (c == null) {
            return "";
        }
        c.moveToFirst();
        return c.getString(c.getColumnIndex(mySQLiteHelper.KEY_NAME));
    }

    public boolean createAccount(String Tablename) {
        this.db.execSQL("CREATE TABLE " + Tablename + mySQLiteHelper.CREATE_TABLE_GENERIC);
        return true;
    }

    public void rawInsert(String sql) {
        this.db.execSQL(sql);
    }

    public Cursor rawQuery(String sql) {
        return this.db.rawQuery(sql, null);
    }

    public void shinny() {
        this.DBHelper.onUpgrade(this.db, 7, 8);
    }
}
