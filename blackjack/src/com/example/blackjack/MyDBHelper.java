package com.example.blackjack;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper{

	static String name = "SystemUser";
	static CursorFactory factory = null;
	static int version = 1;
	public MyDBHelper(Context context) {
	       super(context, name, factory, version);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		String SQL = "CREATE TABLE IF NOT EXISTS  SystemUser (ID INTEGER PRIMARY KEY AUTOINCREMENT, Name Text, Email TEXT, Password Text) ";
		db.execSQL(SQL);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	       String SQL = "DROP TABLE SystemUser ";
	       db.execSQL(SQL);       

	}

}
