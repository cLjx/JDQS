package com.qiushengbao.sms.boradcast_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.qiushengbao.uimain.Homepage;


/**
 * Created by Administrator on 2016/8/30.
 */
public class StartAPP extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"启动APP",Toast.LENGTH_SHORT).show();
        Intent bootStartIntent = new Intent(context,Homepage.class);
        bootStartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(bootStartIntent);
    }
}
