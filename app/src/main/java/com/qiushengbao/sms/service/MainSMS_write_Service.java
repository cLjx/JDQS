package com.qiushengbao.sms.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.qiushengbao.sms.sms_action.ContactInfo;
import com.qiushengbao.sms.sql.Contacts_SQL;

import java.util.List;

/**
 * Created by Administrator on 2016/8/16.
 */
public class MainSMS_write_Service extends IntentService {

    public MainSMS_write_Service(){
        super("MainSMS_write_Service");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        List<ContactInfo> contacts = (List<ContactInfo>)intent.getExtras().getSerializable("contacts");
        Contacts_SQL contacts_sql = new Contacts_SQL(this,"contacts_sql.db",null,1);
        SQLiteDatabase sql_do = contacts_sql.getWritableDatabase();
        sql_do.delete("contacts_table",null,null);
        ContentValues values = new ContentValues();
        for(int i = 0 ; i < contacts.size() ; i++){
            values.put("name", contacts.get(i).getName());
            values.put("number", contacts.get(i).getNumber());
            sql_do.insert("contacts_table",null,values);  
        }
        contacts_sql.close();
    }

}
