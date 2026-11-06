package com.example.electricitybillestimator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "electricity.db";
    private static final int DB_VERSION = 1;

    public DataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE bills(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "month TEXT, units INTEGER, total REAL, rebate REAL, final_cost REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS bills");
        onCreate(db);
    }

    private ContentValues values(String month, int units, double total, double rebate, double fin) {
        ContentValues v = new ContentValues();
        v.put("month", month);
        v.put("units", units);
        v.put("total", total);
        v.put("rebate", rebate);
        v.put("final_cost", fin);
        return v;
    }

    public long insertBill(String month, int units, double total, double rebate, double fin) {
        return getWritableDatabase().insert("bills", null, values(month, units, total, rebate, fin));
    }

    public Cursor getAllBills() {
        return getReadableDatabase().rawQuery(
                "SELECT id, month, final_cost FROM bills ORDER BY id DESC", null);
    }

    public Cursor getBill(int id) {
        return getReadableDatabase().rawQuery(
                "SELECT month, units, total, rebate, final_cost FROM bills WHERE id=?",
                new String[]{String.valueOf(id)});
    }

    public int updateBill(int id, String month, int units, double total, double rebate, double fin) {
        return getWritableDatabase().update("bills", values(month, units, total, rebate, fin),
                "id=?", new String[]{String.valueOf(id)});
    }

    public int deleteBill(int id) {
        return getWritableDatabase().delete("bills", "id=?", new String[]{String.valueOf(id)});
    }
}
