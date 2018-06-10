package com.qiushengbao.sms.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/8/16.
 */
public class Contacts_SQL extends SQLiteOpenHelper {
    Context context = null;
    final String CREATE_TABLE_IN_CONTACTS_DATA =
            "create table contacts_table (_id integer primary " +
            "key autoincrement , name , number )";
    public Contacts_SQL(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context ;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_IN_CONTACTS_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Toast.makeText(context,"数据库升级",Toast.LENGTH_SHORT).show();
    }
}
