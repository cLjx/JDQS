package com.qiushengbao.sms.sms_action;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qsb.qsb2.BaseBaseActivity;
import com.example.qsb.qsb2.R;
import com.qiushengbao.sms.service.MainSMS_write_Service;
import com.qiushengbao.sms.service.SendSMS;
import com.qiushengbao.sms.sql.Contacts_SQL;
import com.qiushengbao.uimain.Homepage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.http.util.EncodingUtils;


/**
 * Created by Administrator on 2016/8/13.
 */
public class MainSMS extends Activity {

    EditText information = null;
    /**
     * 记录listview值
     */
    List<ContactInfo> contacts = null;
    ListView callList = null;
    TextView sendee = null;
    TextView back_cancel = null;
    /**
     * 编辑or全选
     */
    TextView edit_selectAll = null;
    /**
     * 发送信息or删除
     */
    Button send_delete =null;
    /**
     * 编辑or删除模式转换
     */
    boolean is_edit_delete ;

    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BaseBaseActivity.setCurrentActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_main);
        //默认为false，为编辑模式
        this.is_edit_delete = false ;
        this.information = (EditText) findViewById(R.id.id_information);
        information.setText(getString(R.string.info));
        this.sendee = (EditText) findViewById(R.id.id_sendee);
        this.contacts = new ArrayList<>();
        this.back_cancel = (TextView)findViewById(R.id.back_cancel);
        this.edit_selectAll = (TextView)findViewById(R.id.edit_selectAll);
        this.send_delete = (Button)findViewById(R.id.send_delete);
        //获取listView组件
        callList = (ListView)findViewById(R.id.call_list_item);

        this.sharedPreferences = getSharedPreferences("info_sms",0);
        //获得数据
        getContactsFrom_MainActivity();
        showInCallList();
    }

    @Override
    public void onStart() {
        super.onStart();
        ReadInformation(information.toString());
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    /**
     * 返回 到主页面
     */
    public void back(View source) {
        if(back_cancel.getText().equals(getString(R.string.back))){
        	Intent back = new Intent(this, Homepage.class);
            setContactsFrom_MainSMS_Service();
      
            finish();
            return;
        }
        if(back_cancel.getText().equals(getString(R.string.cancel))){
            back_cancel.setText(getString(R.string.back));
            edit_selectAll.setText(getString(R.string.edit));
            send_delete.setText(getString(R.string.send));
            this.is_edit_delete = false ;
            contacts.clear();
            //读取数据库中contacts
            Contacts_SQL contacts_sql = new Contacts_SQL(this,"contacts_sql.db",null,1);
            SQLiteDatabase sql_do = contacts_sql.getReadableDatabase();
            Cursor cursor = sql_do.rawQuery("select * from contacts_table", null);
            while (cursor.moveToNext()){
                ContactInfo c = new ContactInfo(
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("number")),true);
                contacts.add(c);
            }
            showInCallList();
        }
    }

    /**
     * 编辑 or 全选
     */
    public void edit(View source) {
        if(edit_selectAll.getText().equals(getString(R.string.edit))){
            back_cancel.setText(getString(R.string.cancel));
            edit_selectAll.setText(getString(R.string.selectAll));
            send_delete.setText(getString(R.string.delete));
            this.is_edit_delete = true;
            //改变viewList，增加checkbox
            showInCallList();
            return;
        }
        if(edit_selectAll.getText().equals(getString(R.string.selectAll))){
            edit_selectAll.setText(getString(R.string.cancel_selectAll));
            for(int i=0;i<contacts.size();i++)
                contacts.get(i).setIs_check(false);
            //改变viewList，checkbox全选中
            showInCallList();
            return;
        }
        if(edit_selectAll.getText().equals(getString(R.string.cancel_selectAll))){
            edit_selectAll.setText(getString(R.string.selectAll));
            for(int i=0;i<contacts.size();i++)
                contacts.get(i).setIs_check(true);
            //改变viewList，checkbox全选中
            showInCallList();
        }
    }

    /**
     * 保存信息内容
     * @param source
     */
    public void preserve(View source){
        InformationDialog();

    }

    /**
     * 从sendee中添加到contacts
     */
    public void add_from_text(View source) {
        if(sendee.getText()==null)
            return;
        Pattern p = Pattern.compile("1\\d{10}");
        Matcher m = p.matcher( sendee.getText().toString());
        if(m.matches()) {
            ContactInfo contact = new ContactInfo("name", sendee.getText().toString(), true);
            this.contacts.add(contact);
            //显示到viewList中
            showInCallList();
            setContactsFrom_MainSMS_Service();
        }
        else {
            Toast.makeText(this,"请输入有效的电话号码",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 从联系人中添加到contacts
     */
    public void add_from_phone(View source) {
        Intent intent = new Intent(MainSMS.this, Contacts.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contacts", (Serializable) contacts);
        intent.putExtras(bundle);
        startActivityForResult(intent,1);
    }

    /**
     * 发送短信
     */
    public void send(View source) {
        if(send_delete.getText().equals(getString(R.string.send))){
            if(contacts.size() == 0){
                Toast.makeText(this,"请输入有效的电话号码",Toast.LENGTH_SHORT).show();
                return ;
            }
            //TODO
            Log.d("liu","1");
            Intent intent = new Intent(this,SendSMS.class);
            Log.d("liu","2");
            Bundle bundle = new Bundle();
            Log.d("liu","3");
            bundle.putSerializable("contacts", (Serializable) contacts);
            Log.d("liu", "4");
            bundle.putString("information", information.getText().toString());
            Log.d("liu", "5");
            intent.putExtras(bundle);
            Log.d("liu", "6");
            startService(intent);
            return;
        }
        if(send_delete.getText().equals(getString(R.string.delete))){
            edit_selectAll.setText(getString(R.string.edit));
            send_delete.setText(getString(R.string.send));
            back_cancel.setText(getString(R.string.back));
            this.is_edit_delete = false;
            if(contacts.size()==0)
                return;
            //删除选中项
            List<ContactInfo> later_contacts = new ArrayList<>();
            for(ContactInfo c : contacts){
                if(c.getIs_check())
                    later_contacts.add(c);
            }
            contacts.clear();
            contacts = later_contacts ;
            //判断是否完全删除
            for(ContactInfo c : contacts){
                if(!c.getIs_check())
                    Toast.makeText(this,"删除失败",Toast.LENGTH_SHORT).show();
            }
            //改变viewList，去除checkbox
            showInCallList();
            setContactsFrom_MainSMS_Service();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1 && resultCode==1){
            this.contacts = (List<ContactInfo>)data.getExtras().getSerializable("contacts");
            setContactsFrom_MainSMS_Service();
            //显示到viewList中
            showInCallList();
        }
}

    /**
     * 显示到viewList
     */
    private void showInCallList(){
        BaseAdapter callAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return contacts.size();
            }

            @Override
            public Object getItem(int i) {
                return contacts.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                LinearLayout LL = new LinearLayout(MainSMS.this);
                LL.setOrientation(LinearLayout.HORIZONTAL);
                TextView name = new TextView(MainSMS.this);
                name.setTextSize(20);
                
                TextView number = new TextView(MainSMS.this);
                number.setTextSize(18);
                LL.addView(name);
                LL.addView(number);
                CheckBox checkBox_delete = new CheckBox(MainSMS.this);
                if(is_edit_delete){
                    LL.addView(checkBox_delete);
                }
                name.setText(contacts.get(i).getName());
                number.setText(contacts.get(i).getNumber());
                checkBox_delete.setChecked(!contacts.get(i).getIs_check());
                checkBox_delete.setPadding(8, 8, 8, 8);
                if(is_edit_delete){
                    final int item = i;
                    checkBox_delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            contacts.get(item).setIs_check(!contacts.get(item).getIs_check());
                        }
                    });
                }
                return LL;
            }
        };
        callList.setAdapter(callAdapter);
        ReadInformation(information.toString());
    }

    public void getContactsFrom_MainActivity(){
        //获取MainSMS传入的contacts
        Intent intent = getIntent();
        this.contacts = (List<ContactInfo>)intent.getExtras().getSerializable("contacts");
    }

    /**
     * 将修改的数据存入数据库
     */
    private void setContactsFrom_MainSMS_Service(){
        final Intent intent = new Intent(this, MainSMS_write_Service.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("contacts", (Serializable) contacts);
        intent.putExtras(bundle);
        startService(intent);
        TheContactsWillSend.setContacts(contacts);
    }

    /**
     * 读取信息内容
     */
    public void ReadInformation(String info){
            information.setText(sharedPreferences.getString("info_sms","您好，我遇到危险了，发短信向您求救。"));
    }

    public void WriteInformation(String info){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("info_sms",info);
        editor.apply();
    }

    /**
     * 信息编辑对话框
     */
    private void InformationDialog(){
        final EditText editText = new EditText(this);
        editText.setText(information.getText());
        editText.setHint("请输入短信内容");
        editText.setLines(3);
        new AlertDialog.Builder(this)
                .setIcon(null)
                .setTitle("编辑短信内容")
                .setView(editText)
                .setPositiveButton("保存修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        information.setText(editText.getText());
                        WriteInformation(information.getText().toString());
//                        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();

    }

}
