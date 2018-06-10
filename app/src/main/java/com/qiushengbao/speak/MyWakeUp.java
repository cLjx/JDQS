package com.qiushengbao.speak;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.qiushengbao.sms.service.OFF_SendSMS;
import com.qiushengbao.sms.service.SendSMS;
import com.qiushengbao.sms.sms_action.TheContactsWillSend;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

public class MyWakeUp {

    public static final String TAG = MyWakeUp.class.getSimpleName();
    private EventManager mWpEventManager;
    public static Context context;
    static String infoString= null;
    /**
     * ���ѹ��췽��
     * @param context һ�������Ķ���
     */
    public MyWakeUp(Context context,String info) {
        MyWakeUp.context = context;
       infoString = info;
        //create����ʾ��һ����̬����������һ�����ط���EventManagerFactory.create(context, name, version)
        //���ڰٶ��ĵ�û�и��ÿ��������庬�壬����ֻ�ܰ��չ�����demoд��
        mWpEventManager = EventManagerFactory.create(context, "wp");
        //ע������¼�
        mWpEventManager.registerListener(new MyEventListener());
    }
    /**
     * �������ѹ���
     */
     public void start() {
        HashMap<String, String> params = new HashMap<String, String>();
        // ���û�����Դ, ������Դ�뵽 http://yuyin.baidu.com/wake#m4 �������͵���
        params.put("kws-file", "assets:///WakeUp.bin"); 
        mWpEventManager.send("wp.start", new JSONObject(params).toString(), null, 0, 0);
        Log.d(TAG, "----->�����Ѿ���ʼ������");
//        Toast.makeText(context, "--voice start--", Toast.LENGTH_SHORT).show();
        OFF_SendSMS.isStartedMywakeUp = true;
    }
     /**
      * �رջ��ѹ���
      */
     public void stop() {
         // �������İٶ�û�о���˵����������Ҫ���²���
         // send(String arg1, byte[] arg2, int arg3, int arg4)
         mWpEventManager.send("wp.stop", null, null, 0, 0);
         OFF_SendSMS.isStartedMywakeUp = false;
//         Log.d(TAG, "----->�����Ѿ�ֹͣ");
//         Toast.makeText(context, "----->�����Ѿ�ֹͣ", Toast.LENGTH_SHORT).show();
    }
    public class MyEventListener implements EventListener
    {
    	
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {
//        	MainActivity.mVibrator.vibrate(200);
//        	Toast.makeText(MyWakeUp.context, "onEvent", Toast.LENGTH_SHORT).show();
        	
             try {
                 //����json�ļ�
                 JSONObject json = new JSONObject(params);
                 if ("wp.data".equals(name)) { // ÿ�λ��ѳɹ�, ����ص�name=wp.data��ʱ��, ������Ļ��Ѵ���params��word�ֶ�
                     String word = json.getString("word"); // ���Ѵ�
                     /*
                      * �����ҿ��Ը���Լ�������ʵ�ֻ��Ѻ�Ĺ��ܣ��������Ǽ򵥴�ӡ�����Ѵ�
                      */
                     OFF_SendSMS.mVibrator.vibrate(800);
                     Bundle bundle = new Bundle();
                     bundle.putSerializable("contacts", (Serializable) TheContactsWillSend.getContacts());
//                     ReadInformation();
                     bundle.putString("information", MyWakeUp.infoString);
                     Intent i = new Intent(MyWakeUp.context,SendSMS.class);
                     Log.d("liu",bundle.getString("information"));
                     i.putExtras(bundle);
                     MyWakeUp.context.startService(i);
//                     Log.d(TAG,"word--->"+ word);
//                     Toast.makeText(MyWakeUp.context, "-----"+word, Toast.LENGTH_SHORT).show();
                 } else if ("wp.exit".equals(name)) {
                     // �����Ѿ�ֹͣ
//                	 OFF_SendSMS.mVibrator.vibrate(500);
                 }
             } catch (JSONException e) {
//            	 MainActivity.mVibrator.vibrate(500);
            	 Log.d(TAG, "JSONException");
            	 Toast.makeText(MyWakeUp.context, "JSONException", Toast.LENGTH_SHORT).show();
                 throw new AndroidRuntimeException(e);
             }
        }
    }
}
