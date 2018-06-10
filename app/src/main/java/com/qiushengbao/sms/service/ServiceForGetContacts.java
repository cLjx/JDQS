package com.qiushengbao.sms.service;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.qiushengbao.sms.sms_action.ContactInfo;
import com.qiushengbao.sms.sms_action.GettedContectsByService;
import com.qiushengbao.start.Welcome;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceForGetContacts extends IntentService {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS  = 1;


    public ServiceForGetContacts() {
        super("ServiceForGetContacts");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub


        // this.gettedContectsByService = new GettedContectsByService();
        testPermission();

//        doPermission();


    }

    private void doPermission() {
        List<ContactInfo> contactList = new ArrayList<>();
        // 获取手机通讯录信息
        ContentResolver resolver = this.getContentResolver();
        // 获取联系人信息
        Cursor personCursor = resolver.query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        // int size = personCursor.getCount();
        // Toast.makeText(this,size,Toast.LENGTH_SHORT).show();
        // 循环遍历，获取每个联系人的姓名和电话号码
        while (personCursor.moveToNext()) {
            // 联系人id号码
            String ID;
            // 联系人姓名
            String name = "";
            // 联系人电话
            String number = "";
            ID = personCursor.getString(personCursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            name = personCursor.getString(personCursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            // id的整形数据
            int id = Integer.parseInt(ID);
            if (id > 0) {
                // 获取指定id号码的电话号码
                Cursor c = resolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                + "=" + ID, null, null);
                // 遍历游标
                while (c.moveToNext()) {
                    number = c
                            .getString(c
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                c.close();
            }
            Pattern p = Pattern.compile("1\\d{10}");
            Matcher m = p.matcher(number);
            if (m.matches()) {
                ContactInfo contact = new ContactInfo(name, number, false);
                contactList.add(contact);
            }
        }
        personCursor.close();
        GettedContectsByService.setContactList(contactList);

    }

    private void testPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED)
        {
            doPermission();
        }
        else{
            Toast.makeText(this,"read contacts permission deny",Toast.LENGTH_SHORT).show();
        }
    }

}
