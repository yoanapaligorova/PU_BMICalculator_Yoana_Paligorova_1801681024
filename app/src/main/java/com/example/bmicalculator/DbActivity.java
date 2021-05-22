package com.example.bmicalculator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.SQLException;

public abstract class DbActivity extends AppCompatActivity {
   // protected boolean HasNewVersion = true;
    protected void initDB() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                getFilesDir().getPath()+"/bmi.db",
                null
        );
       /* if (HasNewVersion){
            String q = "DROP TABLE if exists BMIRESULTS; ";
            db.execSQL(q);
            HasNewVersion=false;
        }*/
        String createQ="CREATE TABLE if not exists BMIRESULTS (" +
                "ID integer PRIMARY KEY AUTOINCREMENT," +
                "Weight text not null," +
                "Height text not null," +
                "Bmi text not null," +
                "unique(Weight,Height)" +
                ")";
        db.execSQL(createQ);
        db.close();
    }

    protected void SelectSQL(String SelectQ, String[] args, OnSelectSuccess success)
        throws Exception{
        SQLiteDatabase db =
                SQLiteDatabase.openOrCreateDatabase(
                        getFilesDir().getPath()+"/bmi.db",
                        null
                );
        Cursor cursor = db.rawQuery(SelectQ, args);
        while (cursor.moveToNext()){
            String ID =cursor.getString(cursor.getColumnIndex("ID"));
            String Weight =cursor.getString(cursor.getColumnIndex("Weight"));
            String Height =cursor.getString(cursor.getColumnIndex("Height"));
            String Bmi =cursor.getString(cursor.getColumnIndex("Bmi"));
            success.OnElementSelected(ID, Weight, Height, Bmi);
        }
        db.close();
    }

    protected void execSQL(String SQL, Object[] args, OnQuerySuccessOrError successOrError)
        throws SQLException
    {
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openOrCreateDatabase(
                    getFilesDir().getPath()+"/bmi.db",
                    null
            );
            String Q=SQL;
            if (args!=null)
                db.execSQL(Q, args);

            else
                db.execSQL(Q);
                successOrError.OnSuccess();
        }catch (Exception e){
            successOrError.OnError(e.getMessage().toString());
        }finally {
            if (db!=null)
                db.close();
        }
    }



    protected interface OnQuerySuccessOrError{
        public void OnSuccess();
        public void OnError(String error);
    }

    protected interface OnSelectSuccess{
        public void OnElementSelected(String ID, String Weight, String Height, String Bmi);
    }
}
