package com.qiushengbao.sms.sms_action;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qsb.holder.BaseBaseActivity;
import com.example.qsb.holder.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/13.
 */
public class Contacts extends Activity {

    ListView listView = null;
    List<ContactInfo> contacts = null;
    List<ContactInfo> contactList = null;
    ProgressDialog progressDialog =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BaseBaseActivity.setCurrentActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_contacts_list);
        this.listView = (ListView)findViewById(R.id.contacts_list_item);
        //获取MainSMS传入的contacts
        Intent intent = getIntent();
        this.contacts = (List<ContactInfo>)intent.getExtras().getSerializable("contacts");
        this.contactList = new ArrayList<>();

//        this.progressDialog = new ProgressDialog(this);
//        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        this.progressDialog.setMessage("加载联系人中");
//        this.progressDialog.show();

//        final Handler mHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                if (msg.what == 0) {
//                    progressDialog.dismiss();
//                    contactAdapter.notifyDataSetChanged();
//                    progressDialog.dismiss();
//                    onRestart();
//                    Log.d("liu", "onRestart()");
//                }
//            }
//        };
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //获取联系人信息
//                getContactsInformation();
//                mHandler.sendEmptyMessage(0);
//            }
//        });
        //这是采用对话框获得联系人
//        thread.start();
        this.contactList = GettedContectsByService.getContactList();
        getContactsInformation();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("liu", "super.onStart()");
        listView.setAdapter(contactAdapter);
        //每条增加点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                contactList.get(i).setIs_check(!contacts.get(i).getIs_check());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void back(View source){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("contacts", (Serializable) contacts);
        intent.putExtras(bundle);
        Contacts.this.setResult(1, intent);
        Contacts.this.finish();
    }

    public void ok(View source){
        List<ContactInfo> newContaxts = new ArrayList<>();
        for(ContactInfo c : contactList){
            if(c.getIs_check()){
                ContactInfo cInfo = new ContactInfo(c.getName(),c.getNumber(),c.getIs_check());
                newContaxts.add(cInfo);
            }
        }
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("contacts", (Serializable)newContaxts);
        intent.putExtras(bundle);
        Contacts.this.setResult(1, intent);
        Contacts.this.finish();
    }

    /**
     * 获取联系人信息
     */
    private void getContactsInformation(){
//        //获取手机通讯录信息
//        ContentResolver resolver = this.getContentResolver();
//        //获取联系人信息
//        Cursor personCursor = resolver.query
//                (ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//       // int size = personCursor.getCount();
//        //Toast.makeText(this,size,Toast.LENGTH_SHORT).show();
//        //循环遍历，获取每个联系人的姓名和电话号码
//        while (personCursor.moveToNext()){
//            //联系人id号码
//            String ID;
//            //联系人姓名
//            String name = "";
//            //联系人电话
//            String number = "";
//            ID = personCursor.getString
//                    (personCursor.getColumnIndex(ContactsContract.Contacts._ID));
//            name = personCursor.getString
//                    (personCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//            //id的整形数据
//            int id = Integer.parseInt(ID);
//            if(id>0){
//                //获取指定id号码的电话号码
//                Cursor c = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                        null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+ID,null,null);
//                //遍历游标
//                while(c.moveToNext()){
//                    number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                }
//            }
//            Pattern p = Pattern.compile("1\\d{10}");
//            Matcher m = p.matcher(number);
//            if(m.matches()) {
//                ContactInfo contact = new ContactInfo(name, number, false);
//                contactList.add(contact);
//            }
//        }
        for(ContactInfo c1 : contacts)
            for (ContactInfo c2 : contactList)
                if(c1.getNumber().equals(c2.getNumber())){
                    c2.setIs_check(true);
                }
//        personCursor.close();
    }



    private BaseAdapter contactAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public Object getItem(int i) {
            return contactList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LinearLayout LL = new LinearLayout(Contacts.this);
            LL.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout ll = new LinearLayout(Contacts.this);
            ll.setOrientation(LinearLayout.VERTICAL);
            TextView name = new TextView(Contacts.this);
            name.setTypeface(Typeface.SANS_SERIF);
            name.setTextSize(20);
            TextView number = new TextView(Contacts.this);
            number.setTextSize(20);
            ll.addView(name);
            ll.addView(number);
            LL.addView(ll);
            CheckBox is_check = new CheckBox(Contacts.this);
//            is_check.setGravity();
            LL.addView(is_check);
            name.setText(contactList.get(i).getName());
            number.setText(contactList.get(i).getNumber());
            is_check.setChecked(contactList.get(i).getIs_check());
//            is_check.setGravity(Gravity.CENTER);
            final int item = i;
            is_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    contactList.get(item).setIs_check(!contactList.get(item).getIs_check());
                }
            });
            return LL;
        }

    };
}
