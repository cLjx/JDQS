package com.qiushengbao.sms.service;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;

import com.qiushengbao.sms.sms_action.ContactInfo;
import com.qiushengbao.uimain.Homepage;

import java.util.List;

/**
 * Created by Administrator on 2016/8/17.
 */
public class SendSMS extends IntentService {
    Vibrator vibrator = null;

    public SendSMS(){
        super("SendSMS");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        vibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        //TODO
        Log.d("liu","service启动成功");
        List<ContactInfo> contacts = (List<ContactInfo>)intent.getExtras().getSerializable("contacts");
        String information = intent.getExtras().getString("information");
        //TODO
        Log.d("liu","开始发短信了");

        Log.d("liu","开始输出联系人信息：");
        for (ContactInfo c :contacts ){
            Log.d("liu",""+c.getName()+"        "+c.getNumber());
        }
        Log.d("liu",information);
        //TODO 添加地点
//        try {
//        	Thread.sleep(1000);
//        } catch (InterruptedException e) {
//        	// TODO Auto-generated catch block
//        	e.printStackTrace();
//        }
        int aim_user_id = getApplicationContext().getSharedPreferences(
				"private_info", Activity.MODE_PRIVATE)
				.getInt("aim_user_id", -1);
		if(aim_user_id==-1)
			aim_user_id = getApplicationContext().getSharedPreferences(
				"private_info", Activity.MODE_PRIVATE)
				.getInt("user_id", -1);
//        information = "【求生宝 "+aim_user_id+"】"+information;
        for(ContactInfo contact : contacts)
        	sendSMS(information,contact,intent);
        information = "我在：" + Homepage.findedLocation.locationString;
        for(ContactInfo contact : contacts)
        	sendSMS(information,contact,intent);
    }

    private void sendSMS(String information,ContactInfo contact,Intent intent){
        Intent isSend = new Intent("action_isSend_SMS_24h_qiushenggbao");
        Intent isDeliver = new Intent("action_isDeliver_SMS_24h_qiushenggbao");

        PendingIntent isSendPI = PendingIntent.getBroadcast(getApplicationContext(),0,isSend,0);
        PendingIntent isDeliverPI = PendingIntent.getBroadcast(getApplicationContext(),0,isDeliver,0);

        //获取短信管理器
        SmsManager smsManager = SmsManager.getDefault();
        //如果汉字大于70个
        if(information.length() > 70){
            //返回多条短信
            List<String> info = smsManager.divideMessage(information);
            for(String sms:info){
                smsManager.sendTextMessage(contact.getNumber(), null, sms, isSendPI, isDeliverPI);
            }
        }
        else{
            smsManager.sendTextMessage(contact.getNumber(), null, information, isSendPI, isDeliverPI);
        }
        //TODO
        Log.d("liu","发了一条短信了");
        vibrator.vibrate(300);
        
    }
}
/*public void sendTextMessage (
        String destinationAddress, String scAddress, String text,
        PendingIntent sentIntent, PendingIntent deliveryIntent)

destinationAddress	发送短信的地址（也就是号码）
scAddress	短信服务中心，如果为null，就是用当前默认的短信服务中心
text	短信内容
sentIntent	如果不为null，当短信发送成功或者失败时，这个PendingIntent会被广播
        出去成功的结果代码是Activity.RESULT_OK，或者下面这些错误之一  ：RESULT_ERROR_GENERIC_FAILURE
        RESULT_ERROR_RADIO_OFF  RESULT_ERROR_NULL_PDU
        对于 RESULT_ERROR_GENERIC_FAILURE，  the这个sentIntent可能包括额外的"errorCode"，包含一些具
        体有用的信息帮助检查 。基于SMS控制的全部程序检查 sentIntent. 如果 sentIntent 为空，the caller
        will be checked against all unknown applications, which cause smaller number of SMS to
        be sent in checking period.
deliveryIntent	如果不为null，当这个短信发送到接收者那里，这个PendtingIntent会被广播，状态报告生成的pdu
        （指对等层次之间传递的数据单位）会拓展到数据（"pdu"）*/
