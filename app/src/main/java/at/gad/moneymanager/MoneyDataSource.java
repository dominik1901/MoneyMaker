package at.gad.moneymanager;

/**
 * Created by Philipp on 24.10.2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

public class MoneyDataSource {
    private static final String LOG_TAG = MoneyDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private MoneyDBHelper DBHelper;


    private String[] columns = {
            MoneyDBHelper.COLUMN_ID,
            MoneyDBHelper.COLUMN_Number,
            MoneyDBHelper.COLUMN_Category
    };


    public MoneyDataSource(Context context) {
        Log.d(LOG_TAG, "MoneyDataSource erzeugt jetzt MoneyDBHelper.");
        DBHelper = new MoneyDBHelper(context);
    }


    public void open() {
        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = DBHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenbank: " + database.getPath());
    }

    public void close() {
        DBHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }


    public Money CreateMoney(int number, String category) {
        ContentValues values = new ContentValues();
        values.put(MoneyDBHelper.COLUMN_Number, number);
        values.put(MoneyDBHelper.COLUMN_Category, category);

        long insertId = database.insert(MoneyDBHelper.TABLE_Money, null, values);
        Cursor cursor = database.query(MoneyDBHelper.TABLE_Money,
                columns, MoneyDBHelper.COLUMN_ID + "=" + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Money Money = cursorToMoney(cursor);
        cursor.close();

        return Money;
    }

    private Money cursorToMoney(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(MoneyDBHelper.COLUMN_ID);
        int idNumber = cursor.getColumnIndex(MoneyDBHelper.COLUMN_Number);
        int idCategory = cursor.getColumnIndex(MoneyDBHelper.COLUMN_Category);

        String Category = cursor.getString(idCategory);
        int Number = cursor.getInt(idNumber);
        int id = cursor.getInt(idIndex);

        Money Money = new Money (Number, Category);
        return Money;
    }

    public List<Money> getMoney() {
        List<Money> shoppingMemoList = new ArrayList<>();

        Cursor cursor = database.query(MoneyDBHelper.TABLE_Money,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        Money Money;

        while (!cursor.isAfterLast()) {
            Money = cursorToMoney(cursor);
            shoppingMemoList.add(Money);
            Log.d(LOG_TAG, "ID: " + Money.getId() + ", Inhalt: " + Money.toString());
            cursor.moveToNext();
        }

        cursor.close();

        return shoppingMemoList;
    }
}
