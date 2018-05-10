package com.stacktips.speechtotext;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";

    public static final String CONTACTS_TABLE_NAME = "speech";

    public static final String SPEECH_COLUMN_ID = "speechText";

    private HashMap hp;

    public DBHelper(Context context)
    {super(context, DATABASE_NAME, null, 1);}
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // TODO Auto-generated method stub
        db.execSQL("create table speech " +"(id integer primary key, speechText text)"

        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        // TODO Auto-generated method stub

        db.execSQL("DROP TABLE IF EXISTS contacts");

        onCreate(db);

    }

    public boolean insertSpeech (String speechText)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("speechText", speechText);
        db.insert("speech", null, contentValues);
        return true;
    }

    public Cursor getData(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();Cursor res = db.rawQuery( "select * from speech where id="+id+"", null );
        return res;
    }

    public int numberOfRows()
    {

        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);

        return numRows;

    }

    public ArrayList getAllSpeech()

    {
        ArrayList array_list = new ArrayList();

        //hp = new HashMap();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery( "select * from speech", null );

        res.moveToFirst();

        while(res.isAfterLast() == false){



            array_list.add(res.getString(res.getColumnIndex(SPEECH_COLUMN_ID)));

            res.moveToNext();

        }

        return array_list;

    }

}