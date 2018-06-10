package com.qiushengbao.sms.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qsb.holder.BaseBaseActivity;
import com.example.qsb.holder.R;
import com.qiushengbao.sms.sms_action.TheContactsWillSend;
import com.qiushengbao.speak.MyWakeUp;

import java.io.FileInputStream;
import java.io.Serializable;

//import org.apache.http.util.EncodingUtils;

/**
 * Created by Administrator on 2016/8/30.
 */
public class OFF_SendSMS extends Service implements SensorEventListener{

    final IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
    private long firstClickTime;
    
    String information = null;
    
    private SensorManager mSensorManager = null;
    private static final long[] firstAimingTime = new long[2];
    public static Vibrator mVibrator = null;

	public static MyWakeUp myWakeUp = null;
	public static boolean isStartedMywakeUp = true;
	public static boolean isStartedForSpeed = true;
	public static boolean isStartedForSpeedOld = false;

    /**
	 * 不能乱改哈啊，危险
	 */
	final private int[] times = {200,500,1000,1500,2000,2500};
	final private double[] speedsDoubles = {0.5,2.5,3,4,5,6,7,8,9,9.5};

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
//        this.bundle = intent.getExtras();
        registerReceiver(OFFListener,intentFilter);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this,
        		mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
        		SensorManager.SENSOR_DELAY_GAME);
        mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        ReadInformation();
        myWakeUp = new MyWakeUp(this,information);
//        myWakeUp.start();
        SharedPreferences sharedPreferences = getSharedPreferences("private_info",Activity.MODE_PRIVATE);
//        newTimeForSpeed = sharedPreferences.getInt("speed_times", 4);
//		newSpeedForSpeed  = sharedPreferences.getInt("speed_speed", 4);
		isStartedForSpeed  = sharedPreferences.getBoolean("ischeck_speed", true);
		isStartedForSpeedOld  = sharedPreferences.getBoolean("ischeck_speed_old", false);
		isStartedMywakeUp  = sharedPreferences.getBoolean("ischeck_speak", true);
		if(isStartedMywakeUp)
			myWakeUp.start();
       return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onDestroy(){
        Intent localIntent = new Intent();
        localIntent.setClass(this, OFF_SendSMS.class); // 销毁时重新启动Service
        this.startService(localIntent);
        mSensorManager.unregisterListener(this);
        if(OFFListener != null)
            unregisterReceiver(OFFListener);
    }

    private BroadcastReceiver OFFListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("liu","--------------------------------------------------");
            if(firstClickTime > 0){
                if(System.currentTimeMillis() - firstClickTime < 2000){
                    Log.d("liu", "-----------------双击啦---------------------------");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("contacts", (Serializable) TheContactsWillSend.getContacts());
//                    ReadInformation();
                    bundle.putString("information", information);
                    Intent i = new Intent(context,SendSMS.class);
                    Log.d("liu",bundle.getString("information"));
                    i.putExtras(bundle);
                    startService(i);
                    firstClickTime = 0;

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            record();
//                        }
//                    });

//                    mMediaRecorder01.stop();
//                    mMediaRecorder01.release();
//                    mMediaRecorder01 = null;

                    return;
                }
            }
            firstClickTime = System.currentTimeMillis();
        }
    };

    private void ReadInformation(){
        try {
            FileInputStream fis = openFileInput("information.txt");
            //获取文件长度
            int length = fis.available();
            byte[] b =new byte[length];
            fis.read(b);
            fis.close();
            //将数组转换为指定格式的字符串,并设置为information显示的字符串
//            this.information = EncodingUtils.getString(b, "UTF-8");
            this.information = String.valueOf(b);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(information == null)
            information = getString(R.string.info);
    }

	@Override
	public void onSensorChanged(SensorEvent event) {
        if(isStartedForSpeedOld){
            //老人滑到监控处理
            actionForSensorChanged(0,event,0,0);
		}
        if(isStartedForSpeed){
            //原来的加速度
            actionForSensorChanged(1,event,4,4);
        }
	}

    private void actionForSensorChanged(int index,SensorEvent event,int time,int speed) {
        if(firstAimingTime[index] > 0){
            float[] value = event.values;
//			Toast.makeText(OFF_SendSMS.this, value[0]+" - "+value[1], Toast.LENGTH_SHORT).show();

            if(value[0]*value[0]+value[1]*value[1]+value[2]*value[2]<speedsDoubles[speed]*speedsDoubles[speed]){
//				if(value[0]*value[0]+value[1]*value[1]+value[2]*value[2]<1*1){
                firstAimingTime[index] = 0;
                return;
            }
            if(System.currentTimeMillis() - firstAimingTime[index] > times[time]){
                if(index==0) {
                    actionForOlderSliping();
                    return;
                }
                Toast.makeText(this,"撞车了", Toast.LENGTH_LONG).show();
                mVibrator.vibrate(800);
                Bundle bundle = new Bundle();
                bundle.putSerializable("contacts", (Serializable) TheContactsWillSend.getContacts());
//                ReadInformation();
                bundle.putString("information", information+"而且很有可能是出车祸了。");
                Intent i = new Intent(this,SendSMS.class);
                Log.d("liu",bundle.getString("information"));
                i.putExtras(bundle);
                startService(i);
                firstAimingTime[index] = 0;
                return;
            }
        }
        firstAimingTime[index] = System.currentTimeMillis();
    }

    private void actionForOlderSliping() {
        mVibrator.vibrate(100);
//        Toast.makeText(BaseBaseActivity.getCurrentActivity(),"掉了",Toast.LENGTH_LONG).show();
        if(extendThreadForSlipingSendSMS)
            return;
//        if(CreatDiag.getInstance().alert==null)
//            CreatDiag.getInstance().alert.create();
        extendThreadForSlipingSendSMS=true;
        CreatDiag.getDialogInstance().show();
        ssssstopForThread=false;
        timeForSlipingSendSMS=31;
        new Thread(new Runnable() {
            @Override
            public void run() {
                 while (OFF_SendSMS.timeForSlipingSendSMS>0 && !OFF_SendSMS.ssssstopForThread){
                    try {
                        OFF_SendSMS.timeForSlipingSendSMS=OFF_SendSMS.timeForSlipingSendSMS-1;
                        Thread.sleep(1000);
                        final int time = OFF_SendSMS.timeForSlipingSendSMS-1;

                        if(time>0) {
                            BaseBaseActivity.getCurrentActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    OFF_SendSMS.CreatDiag.getTVInstance().setText("系统断定您已摔倒\n将于" + time + "秒后发生短信。");
//                                CreatDiag.getInstance().alert.notifyAll();
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(OFF_SendSMS.ssssstopForThread) {
                    BaseBaseActivity.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BaseBaseActivity.getCurrentActivity(),"已停止发送短信", Toast.LENGTH_SHORT).show();
                        }
                    });
                    OFF_SendSMS.ssssstopForThread=true;
                    OFF_SendSMS.extendThreadForSlipingSendSMS=false;
                    OFF_SendSMS.timeForSlipingSendSMS=30;
                    return;
                }
                OFF_SendSMS.CreatDiag.getDialogInstance().dismiss();
//                Toast.makeText(BaseBaseActivity.getCurrentActivity(),"摔倒了", Toast.LENGTH_LONG).show();
                mVibrator.vibrate(100);
                Bundle bundle = new Bundle();
                bundle.putSerializable("contacts", (Serializable) TheContactsWillSend.getContacts());
//                ReadInformation();
                bundle.putString("information", information+"而且很有可能是摔倒了。");
                Intent i = new Intent(BaseBaseActivity.getCurrentActivity(),SendSMS.class);
                Log.d("liu",bundle.getString("information"));
                i.putExtras(bundle);
                startService(i);
                OFF_SendSMS.ssssstopForThread=true;
                extendThreadForSlipingSendSMS=false;
                timeForSlipingSendSMS=31;
            }
        }).start();
    }

/*    public static  AlertDialog.Builder alert = new AlertDialog.Builder(BaseBaseActivity.getCurrentActivity())
            .setTitle("提示：")
            .setMessage("系统断定您已摔倒\n将于30秒后发生短信。")
            .setPositiveButton("立即发送",new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    timeForSlipingSendSMS = 0;
                }
            })
            .setNegativeButton("取消发送", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ssssstopForThread = true;
                }
            });*/
    private static int timeForSlipingSendSMS = 31;
    private static boolean ssssstopForThread = false;
    private static boolean extendThreadForSlipingSendSMS = false;


    @Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

//    private void record(){
//        try {
//        File filedir = new File(getFilesDir(),"demo");
//        Log.d("liu","点击了3");
//        File fileName = new File(filedir,getDefaultName()+".amr");
//        fileName.createNewFile();
//        Log.d("liu","点击了4");
//        String path = fileName.getAbsolutePath();
//        //创建文件，使用自己指定文件名(这里我手动创建好了,我们也可以利用mkdirs的方法来创建)
//        Log.d("liu","点击了5");
//        File myRecAudioFile = new File(path);
//        Log.d("liu","点击了6");
//        mMediaRecorder01 = new MediaRecorder();
//        Log.d("liu", "点击了7");
//          /* 设置录音来源为麦克风 */
//        mMediaRecorder01
//                .setAudioSource(MediaRecorder.AudioSource.MIC);
//        Log.d("liu", "点击了8");
//        mMediaRecorder01
//                .setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        Log.d("liu", "点击了9");
//        mMediaRecorder01
//                .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        Log.d("liu", "点击了10");
//        //文件保存位置
//        mMediaRecorder01.setOutputFile(myRecAudioFile
//                .getAbsolutePath());
//        Log.d("liu", "点击了11");
//        mMediaRecorder01.prepare();
//        Log.d("liu", "点击了12");
//        mMediaRecorder01.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    private String getDefaultName(){
//        long time =System.currentTimeMillis();
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String str = format.format(new Date(time));
//        return str;
//    }

//之前的加速度的检测的代码
    /*if(firstAimingTime > 0){
        float[] value = event.values;
//			Toast.makeText(OFF_SendSMS.this, value[0]+" - "+value[1], Toast.LENGTH_SHORT).show();

        if(value[0]*value[0]+value[1]*value[1]+value[2]*value[2]<speedsDoubles[newSpeedForSpeed]*speedsDoubles[newSpeedForSpeed]){
//				if(value[0]*value[0]+value[1]*value[1]+value[2]*value[2]<1*1){
            firstAimingTime = 0;
            return;
        }
        if(System.currentTimeMillis() - firstAimingTime > times[OFF_SendSMS.newTimeForSpeed]){
            Toast.makeText(this,"撞车了", Toast.LENGTH_LONG).show();
            mVibrator.vibrate(800);
            Bundle bundle = new Bundle();
            bundle.putSerializable("contacts", (Serializable) TheContactsWillSend.getContacts());
//                ReadInformation();
            bundle.putString("information", information+"而且很有可能是出车祸了。");
            Intent i = new Intent(this,SendSMS.class);
            Log.d("liu",bundle.getString("information"));
            i.putExtras(bundle);
            startService(i);
            firstAimingTime = 0;
            return;
        }
    }
    firstAimingTime = System.currentTimeMillis();*/
	
	
	static class CreatDiag{
        public TextView tv = new TextView(BaseBaseActivity.getCurrentActivity());

        public AlertDialog alert = new AlertDialog.Builder(BaseBaseActivity.getCurrentActivity())
                .setTitle("提示：")
                .setView(tv)
                .setPositiveButton("立即发送",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OFF_SendSMS.timeForSlipingSendSMS=0;
                    }
                })
                .setNegativeButton("取消发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OFF_SendSMS.ssssstopForThread=true;
                    }
                }).create();

        private static final CreatDiag creatDiag = new CreatDiag();

        private CreatDiag(){
        }

        public static AlertDialog getDialogInstance(){
            return creatDiag.alert;
        }
        public static TextView getTVInstance(){
            creatDiag.tv.setBackgroundColor(Color.argb(0,1,0,0));
            creatDiag.tv.setTextSize(30);
            creatDiag.tv.setPadding(7,14,7,14);
            return creatDiag.tv;
        }
    }
	
}
