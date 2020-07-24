/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hyphenate.chatuidemo.receiver;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.fanju.utils.TgSystem;
import com.hyphenate.chatuidemo.ui.MainActivity;
import com.hyphenate.chatuidemo.ui.SplashActivity;
import com.hyphenate.chatuidemo.ui.VideoCallActivity;
import com.hyphenate.chatuidemo.ui.VoiceCallActivity;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.EasyUtils;

import java.util.List;
import java.util.Set;

public class CallReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
        if (!DemoHelper.getInstance().isLoggedIn())
            return;
        //username
        String from = intent.getStringExtra("from");
        //call type
        String type = intent.getStringExtra("type");

        //todo 本地->接收呼叫扩展内容
        String callExt = EMClient.getInstance().callManager().getCurrentCallSession().getExt();

        Log.e("CallReceiver", "callExt1:" + callExt);
        Bundle bundle = intent.getExtras();
        Set<String> keySet = bundle.keySet();  //获取所有的Key,

        for (String key : keySet) {
            Log.e("CallReceiver", "KEY:" + key);
        }


        if (!TgSystem.isRunningForeground(context)) {
            Log.d("CallReceiver", "在后台接受");
            Intent fullScreenIntent;
            String content = "";
            if ("video".equals(type)) { //video call
                fullScreenIntent = new Intent(context, VideoCallActivity.class);
                content = context.getString(R.string.alert_request_video, from);
            } else {
                fullScreenIntent = new Intent(context, VoiceCallActivity.class);
                content = context.getString(R.string.alert_request_voice, from);
            }
            fullScreenIntent.putExtra("ex_message", callExt);
            fullScreenIntent.putExtra("username", from)
                    .putExtra("isComingCall", true).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            DemoHelper.getInstance().getNotifier().notify(fullScreenIntent, "来电提示", content);

            isRunningForegroundToApp(context, from, type,callExt);
           // TgSystem.setTopApp(context);

//            wakeUpAndUnlock(context);
//            try
//            {
//                Thread.sleep(500);
//            }
//            catch (Exception ex){
//
//            }

            //isRunningForegroundToApp(context, from, type,callExt);

        } else {
            startTargetActivity(context, from, type,callExt);
        }

        EMLog.d("CallReceiver", "app received a incoming call");
    }

    private void startTargetActivity(Context context, String from, String type,String ex_message) {
        if("video".equals(type)){ //video call
            context.startActivity(new Intent(context, VideoCallActivity.class).
                    putExtra("username", from).putExtra("isComingCall", true).
                    putExtra("ex_message", ex_message).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


        }else{ //voice call
            context.startActivity(new Intent(context, VoiceCallActivity.class).
                    putExtra("username", from).putExtra("isComingCall", true).
                    putExtra("ex_message", ex_message).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }


    private  void isRunningForegroundToApp(Context context, String from, String type,String ex_message) {
        Log.d("CallReceiver", "isRunningForegroundToApp");
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //利用系统方法获取当前Task堆栈, 数目可按实际情况来规划，这里只是演示
        List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(20);

        Log.d("CallReceiver", "taskInfoList.size:"+taskInfoList.size());
        for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
            //遍历找到本应用的 task，并将它切换到前台

            Log.d("CallReceiver", "taskInfo  pid " + taskInfo.id);
            Log.d("CallReceiver", "taskInfo  processName " + taskInfo.topActivity.getPackageName());
            Log.d("CallReceiver", "taskInfo  getPackageName " + context.getPackageName());

            if (taskInfo.baseActivity.getPackageName().equals(context.getPackageName())) {

                Log.d("CallReceiver", "my  pid " + taskInfo.id);
                Log.d("CallReceiver", "my  processName " + taskInfo.topActivity.getPackageName());
                Log.d("CallReceiver", "my  getPackageName " + context.getPackageName());

                activityManager.moveTaskToFront(taskInfo.id, 0);

                Intent intent = new Intent(context, VideoCallActivity.class);
                intent.putExtra("username", from).putExtra("isComingCall", true).
                        putExtra("ex_message", ex_message);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(intent);
                break;
            }
        }
    }

//    public static void wakeUpAndUnlock(Context context){
//        //屏锁管理器
//        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
//        //解锁
//        kl.disableKeyguard();
//        //获取电源管理器对象
//        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
//        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
//        //点亮屏幕
//        wl.acquire();
//        //释放
//        wl.release();
//    }

}
