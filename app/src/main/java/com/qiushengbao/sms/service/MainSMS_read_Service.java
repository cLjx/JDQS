package com.qiushengbao.sms.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;

import com.qiushengbao.sms.sms_action.ContactInfo;
import com.qiushengbao.sms.sql.Contacts_SQL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/16.
 */
public class MainSMS_read_Service extends Service {
    private List<ContactInfo> contacts;
    private Mybinder mybinder = new Mybinder();
    Contacts_SQL contacts_sql = null;

    //通过获得ibinder对象
    public class Mybinder extends Binder {
        public List<ContactInfo> getContacts()
        {
            return contacts;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mybinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //创建了一个DatabaseHelper对象
        this.contacts = new ArrayList<>();
        contacts_sql = new Contacts_SQL(this,"contacts_sql.db",null,1);
        SQLiteDatabase sql_do = contacts_sql.getReadableDatabase();
        Cursor cursor = sql_do.rawQuery("select * from contacts_table", null);
        while (cursor.moveToNext()){
            ContactInfo c = new ContactInfo(
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("name")),true);
            this.contacts.add(c);
        }
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        contacts_sql.close();
        super.onDestroy();
    }


}

