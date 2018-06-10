package com.qiushengbao.start;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.qsb.qsb2.R;
import com.qiushengbao.sms.service.ServiceForGetContacts;
import com.qiushengbao.uimain.Homepage;

public class Welcome extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 1000;
	private final static int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_welcome);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
					Intent intent=new Intent();
					intent.setClass(Welcome.this,Homepage.class);
					Welcome.this.startActivity(intent);
					Welcome.this.finish();
			}
		},SPLASH_DISPLAY_LENGHT);
	}

	private void doPermission() {
        startService(new  Intent(this,ServiceForGetContacts.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                doPermission();
            } else
            {
                // Permission Denied
                Toast.makeText(Welcome.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}

