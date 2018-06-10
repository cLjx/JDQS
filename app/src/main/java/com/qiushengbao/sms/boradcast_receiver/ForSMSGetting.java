package com.qiushengbao.sms.boradcast_receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.widget.Toast;

public class ForSMSGetting extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
//		Toast.makeText(context, "duanxing\n", Toast.LENGTH_LONG).show();
//		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
			Bundle bundle = new Bundle();
			if(bundle==null)
				return;
			Object[] pdus = (Object[])bundle.get("pdus");
			if(pdus==null)
				return;
			String sender = null;
			String content = null;
			SmsMessage[] smsMessage = new SmsMessage[pdus.length];
			for(int i=0;i<pdus.length;i++){
				smsMessage[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
				sender = smsMessage[i].getDisplayOriginatingAddress();
				content = smsMessage[i].getDisplayOriginatingAddress();
				Toast.makeText(context, "uafsdfsadfsdfsdfnxing\n"+sender+"\n"+content, Toast.LENGTH_LONG).show();
				if(!content.startsWith("【求生宝"))
					return;
				String newIdString = content.substring(5, 9);
				SharedPreferences sharedPreferences = context.getSharedPreferences(
						"private_info", Activity.MODE_PRIVATE);
				String listAimUser = sharedPreferences.getString("list_aim_user_id_tel", "");
				Editor editor = sharedPreferences.edit();
				if(listAimUser.indexOf(""+newIdString+",")==-1){//不存在序列中
					editor.putString("list_aim_user_id_tel", listAimUser+";"+newIdString+","+sender);
				}
				editor.putInt("aim_user_id",Integer.parseInt(newIdString));
				editor.apply();
				Toast.makeText(context, ""+newIdString, Toast.LENGTH_SHORT).show();

			}
//		}
	}

}
