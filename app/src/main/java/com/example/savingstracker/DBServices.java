package com.example.savingstracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

public class DBServices extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LinGinSavingsDB.db";

    public DBServices(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table MainAccount"+
                "(UID INTEGER primary key autoincrement, " +
                "BankAmount REAL Default 0.00, "+
                "BankSavings REAL Default 0.00, " +
                "Goal REAL not null, " +
                "CashSavings REAL Default 0.00" +
                ");");
        db.execSQL("create table Transactions(" +
                "TID INTEGER primary key autoincrement, " +
                "UID INTEGER, " +
                "AmountTransferred REAL not null," +
                "Date INTEGER," +
                "FOREIGN KEY(UID)REFERENCES MainAccount(UID)" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Transactions;");
        db.execSQL("DROP TABLE IF EXISTS MainAccount;");
    }

    public Cursor getMainAccount(){
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("Select * from MainAccount;", null);
        cursor.moveToFirst();
        return cursor;
    }

    public void insertInitialAccount(float goal){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("insert into MainAccount (Goal) values ("+goal+");");
    }

    public void updateAccount(Account account){
        this.getWritableDatabase().execSQL("" +
                "UPDATE MainAccount " +
                "SET BankAmount = " + account.get_bankAmount() + "," +
                "BankSavings = " + account.get_savingAmount() + "," +
                "Goal = " + account.get_goal() + ","+
                "CashSavings = " + account.get_cashAmount() +
                " WHERE UID = " + account.get_id() + ";");
    }

    public void insertTransaction(int uID, float amount){
        this.getWritableDatabase().execSQL(
                "INSERT INTO Transactions (UID, AmountTransferred, Date )" +
                        "VALUES (" +
                        uID+","+
                        amount+","+
                        Calendar.getInstance().getTimeInMillis() +");"
        );
    }

    public String toStringTransaction(){
        StringBuilder builder = new StringBuilder();
        @SuppressLint("Recycle") Cursor cursor = this.getReadableDatabase().rawQuery(
                "Select * from Transactions;",null
        );
        while(cursor.moveToNext()){
            for(int i = 0; i<cursor.getColumnCount(); i++){
                builder.append(cursor.getColumnName(i)).append(":")
                        .append(cursor.getString(i)).append(",");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
