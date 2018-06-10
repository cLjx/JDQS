package com.qiushengbao.sms.boradcast_receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/8/17.
 */
public class IsSendOrDeliver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("action_isSend_SMS_24h_qiushenggbao")) {
            try {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //TODO
                        Log.d("liu", "发送短信成功");
                        //发送短信成功
//                        Toast.makeText(context,"发送短信成功", Toast.LENGTH_SHORT).show();

                        break;
                    default:
                        //TODO
                        Log.d("liu", "发送短信失败");
                        //发送短信失败
                        Toast.makeText(context,"发送短信失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
        else if (intent.getAction().equals("action_isDeliver_SMS_24h_qiushenggbao")) {
            try {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        //TODO
                        Log.d("liu", "对方已接受短信");
                        //接收短信成功
//                        Toast.makeText(context,"对方已接受短信", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        //TODO
                        Log.d("liu", "对方未接受短信");
                        // 短信未送达
                        Toast.makeText(context,"对方未接受短信", Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }
}
