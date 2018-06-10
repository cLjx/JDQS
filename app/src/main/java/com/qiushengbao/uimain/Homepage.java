package com.qiushengbao.uimain;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.qsb.qsb2.BaseBaseActivity;
import com.example.qsb.qsb2.R;
import com.qiushengbao.sms.service.OFF_SendSMS;
import com.qiushengbao.sms.service.ServiceForGetContacts;
import com.qiushengbao.sms.sms_action.ContactInfo;
import com.qiushengbao.sms.sms_action.FindLocation;
import com.qiushengbao.sms.sms_action.MainSMS;
import com.qiushengbao.sms.sms_action.TheContactsWillSend;
import com.qiushengbao.sms.sql.Contacts_SQL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Homepage extends AppCompatActivity implements View.OnClickListener {

    private Button sos;

	public static FindLocation findedLocation = null;

	static List<ContactInfo> contacts = null;
	Intent intent_sendSMS_service = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		BaseBaseActivity.setCurrentActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

        this.sos = (Button) findViewById(R.id.sos);
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		this.contacts = new ArrayList<>();
        intent_sendSMS_service = new Intent(this, OFF_SendSMS.class);
        sos.setOnClickListener(this);
        //TODO 6.0 permission
		testPermission();
	}


	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}


	// 退出
	private static Boolean isExit = false;
	private static Boolean hasTask = false;
	Timer tExit = new Timer();
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			isExit = true;
			hasTask = true;
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isExit == false) {
				isExit = true;
				Toast.makeText(Homepage.this, "再按一次退出程序", Toast.LENGTH_SHORT)
						.show();
				if (!hasTask) {
					tExit.schedule(task, 2000);
				}
			} else {
				finish();
				System.exit(0);
			}
		}
		return false;
	}

	@Override
	// 主界面点击事件
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.sos:
				Intent intent_sendSMS = new Intent(this, MainSMS.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("contacts", (Serializable) contacts);
//			bundle.putSerializable("binder", (Serializable) myBinder);
				intent_sendSMS.putExtras(bundle);
				startActivity(intent_sendSMS);
				break;

			default:
				break;
		}

	}

	private void readSQL() {

		contacts.clear();
		// 读取数据库中contacts
		Contacts_SQL contacts_sql = new Contacts_SQL(this, "contacts_sql.db",
				null, 1);
		SQLiteDatabase sql_do = contacts_sql.getReadableDatabase();
		Cursor cursor = sql_do.rawQuery("select * from contacts_table", null);
		while (cursor.moveToNext()) {
			ContactInfo c = new ContactInfo(cursor.getString(cursor
					.getColumnIndex("name")), cursor.getString(cursor
					.getColumnIndex("number")), true);
			this.contacts.add(c);
		}
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        readSQL();
		TheContactsWillSend.setContacts(contacts);
	}


	private void doPermission(String str) {
        if(str.equals("contacts")) {
            startService(new Intent(this, ServiceForGetContacts.class));
        }
        if(str.equals("location")) {
            findedLocation = new FindLocation(this);
        }
        if(str.equals("sms")) {
            if (intent_sendSMS_service != null)
                stopService(intent_sendSMS_service);
            startService(intent_sendSMS_service);
        }
	}

	void firstPermission(){
//        int[] a ={1,2,3};
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},6);
    }

	private void testPermission() {
        firstPermission();

		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_CONTACTS)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.READ_CONTACTS}, 1);
		} else {
			doPermission("contacts");
		}

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        } else {
            doPermission("location");
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 3);
        } else {
            doPermission("sms");
        }
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO 纯属乱搞
//
//        doPermission("contacts");
//        doPermission("location");
//        doPermission("sms");
//        if(2>1)
//            return;
		if (requestCode == 1) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				doPermission("contacts");
			} else {
				Toast.makeText(Homepage.this, "contacts Permission Denied", Toast.LENGTH_SHORT).show();
			}
			return;
		}
        else if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doPermission("location");
            } else {
                Toast.makeText(Homepage.this, "location Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        else if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doPermission("sms");
            } else {
                Toast.makeText(Homepage.this, "sms Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }

//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}


}
