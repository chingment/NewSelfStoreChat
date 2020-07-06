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
package com.hyphenate.chatuidemo.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.hyphenate.chat.EMCallSession;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCustomMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoCallHelper;
import com.hyphenate.chatuidemo.DemoHelper;
import com.hyphenate.chatuidemo.R;
import com.hyphenate.chatuidemo.utils.PhoneStateManager;
import com.hyphenate.chatuidemo.utils.PreferenceManager;
import com.hyphenate.easeui.fanju.ProductSkuAdapter;
import com.hyphenate.easeui.fanju.model.CustomMsg;
import com.hyphenate.easeui.fanju.model.ProductSkuBean;
import com.hyphenate.easeui.fanju.model.MsgContentByBuyInfo;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.media.EMCallSurfaceView;
import com.hyphenate.util.EMLog;
import com.hyphenate.chat.EMWaterMarkOption;
import com.hyphenate.chat.EMWaterMarkPosition;
import com.superrtc.sdk.VideoView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;


public class VideoCallActivity extends CallActivity implements OnClickListener {

    private boolean isMuteState;
    private boolean isHandsfreeState;
    private boolean isAnswered;
    private boolean isEndCallTriggerByMe = false;
    private boolean isMonitor = true;
    private boolean isInCalling;

    // 视频通话画面显示控件，这里在新版中使用同一类型的控件，方便本地和远端视图切换
    protected EMCallSurfaceView surface_sv_local;
    protected EMCallSurfaceView surface_sv_opposite;
    private int surface_state = -1;

    private TextView call_tv_state;
    private TextView call_tv_monitor;
    private TextView call_tv_nickname;
    private TextView call_tv_networkstatus;
    private Chronometer call_chm_tick;
    private TextView call_tv_is_p2p;

    private Button call_btn_hangup;
    private LinearLayout call_coming_ll;
    private Button call_coming_btn_refusecall;
    private Button call_coming_btn_answercall;

    private LinearLayout voice_ll;
    private ImageView iv_mute;
    private ImageView iv_handsfree;
    private ImageView iv_switchcamera;

    private Handler uiHandler;
    private EMVideoCallHelper callHelper;

    private Bitmap watermarkbitmap;
    private EMWaterMarkOption watermark;

    private LinearLayout buyinfo_ll_info;
    private LinearLayout buyinfo_ll_btns;
    private GridView buyinfo_list_skus;
    private TextView buyinfo_tv_storename;
    private Button buyinfo_btn_butongyi;
    private Button buyinfo_btn_tongyi;

    private String customMsg_Type;
    private CustomMsg<MsgContentByBuyInfo> customMsg_BuyInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
        	finish();
        	return;
        }
        setContentView(R.layout.em_activity_video_call);

        DemoHelper.getInstance().isVideoCalling = true;

        callType = 1;

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        uiHandler = new Handler();

        call_chm_tick = (Chronometer) findViewById(R.id.call_chm_tick);
        call_tv_state = (TextView) findViewById(R.id.call_tv_state);
        call_tv_monitor = (TextView) findViewById(R.id.call_tv_monitor);
        call_tv_nickname = (TextView) findViewById(R.id.call_tv_nickname);
        call_tv_networkstatus = (TextView) findViewById(R.id.call_tv_networkstatus);
        call_tv_is_p2p=(TextView)findViewById(R.id.call_tv_is_p2p);


        call_btn_hangup = (Button) findViewById(R.id.call_btn_hangup);
        call_btn_hangup.setOnClickListener(this);

        call_coming_ll = (LinearLayout) findViewById(R.id.call_coming_ll);
        call_coming_btn_refusecall = (Button) findViewById(R.id.call_coming_btn_refusecall);
        call_coming_btn_refusecall.setOnClickListener(this);
        call_coming_btn_answercall = (Button) findViewById(R.id.call_coming_btn_answercall);
        call_coming_btn_answercall.setOnClickListener(this);

        voice_ll = (LinearLayout) findViewById(R.id.voice_ll);
        iv_mute = (ImageView) findViewById(R.id.iv_mute);
        iv_mute.setOnClickListener(this);
        iv_handsfree = (ImageView) findViewById(R.id.iv_handsfree);
        iv_handsfree.setOnClickListener(this);
        iv_switchcamera = (ImageView) findViewById(R.id.iv_switchcamera);
        iv_switchcamera.setOnClickListener(this);

        buyinfo_ll_info= (LinearLayout) findViewById(R.id.buyinfo_ll_info);
        buyinfo_tv_storename=(TextView) findViewById(R.id.buyinfo_tv_storename);
        buyinfo_list_skus=(GridView) findViewById(R.id.buyinfo_list_skus);
        buyinfo_list_skus.setFocusable(false);
        buyinfo_list_skus.setClickable(false);
        buyinfo_ll_btns= (LinearLayout) findViewById(R.id.buyinfo_ll_btns);
        buyinfo_btn_tongyi = (Button) findViewById(R.id.buyinfo_btn_tongyi);
        buyinfo_btn_tongyi.setOnClickListener(this);
        buyinfo_btn_butongyi = (Button) findViewById(R.id.buyinfo_btn_butongyi);
        buyinfo_btn_butongyi.setOnClickListener(this);



        msgid = UUID.randomUUID().toString();
        isInComingCall = getIntent().getBooleanExtra("isComingCall", false);
        username = getIntent().getStringExtra("username");
        ex_message=getIntent().getStringExtra("ex_message");
        call_tv_nickname.setText(username);


        Log.i(TAG,"ex_message:"+ex_message);

        if(ex_message!=null) {
            if (ex_message.contains("\"type\":\"buyinfo\"")) {
                CustomMsg<MsgContentByBuyInfo> rt = JSON.parseObject(ex_message, new TypeReference<CustomMsg<MsgContentByBuyInfo>>() {
                });
                if (rt != null) {
                    MsgContentByBuyInfo buyInfo = rt.getContent();
                    if (buyInfo != null) {
                        customMsg_Type = rt.getType();
                        customMsg_BuyInfo=rt;
                        buyinfo_ll_info.setVisibility(View.VISIBLE);
                        buyinfo_tv_storename.setText(buyInfo.getStoreName());
                        if (buyInfo.getSkus() != null) {
                            ProductSkuAdapter productKindSkuAdapter = new ProductSkuAdapter(this, rt.getContent().getSkus());
                            buyinfo_list_skus.setAdapter(productKindSkuAdapter);
                        }
                    }
                }
            }
        }

        //获取水印图片
        if(PreferenceManager.getInstance().isWatermarkResolution()) {
            try {
                InputStream in = this.getResources().getAssets().open("watermark.png");
                watermarkbitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            watermark = new EMWaterMarkOption(watermarkbitmap, 75, 25, EMWaterMarkPosition.TOP_RIGHT, 8, 8);
        }

        // local surfaceview
        surface_sv_local = (EMCallSurfaceView) findViewById(R.id.surface_sv_local);
        surface_sv_local.setOnClickListener(this);
        surface_sv_local.setZOrderMediaOverlay(true);
        surface_sv_local.setZOrderOnTop(true);

        // remote surfaceview
        surface_sv_opposite = (EMCallSurfaceView) findViewById(R.id.surface_sv_opposite);
        surface_sv_opposite.setScaleMode(VideoView.EMCallViewScaleMode.EMCallViewScaleModeAspectFill);

        // set call state listener
        addCallStateListener();

        if (!isInComingCall) { // outgoing call

            call_coming_ll.setVisibility(View.INVISIBLE);
            call_btn_hangup.setVisibility(View.VISIBLE);
            call_tv_state.setText("正在呼叫对方");

            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(this, R.raw.em_outgoing, 1);


            EMClient.getInstance().callManager().setSurfaceView(surface_sv_local, surface_sv_opposite);
            handler.sendEmptyMessage(MSG_CALL_MAKE_VIDEO);
            handler.postDelayed(new Runnable() {
                public void run() {
                    streamID = playMakeCallSounds();
                }
            }, 300);

        } else { // incoming call
            call_coming_ll.setVisibility(View.VISIBLE);
            call_btn_hangup.setVisibility(View.INVISIBLE);
            call_tv_state.setText("对方请求接听");

            if(EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.IDLE
                    || EMClient.getInstance().callManager().getCallState() == EMCallStateChangeListener.CallState.DISCONNECTED) {
                // the call has ended
                finish();
                return;
            }
            voice_ll.setVisibility(View.INVISIBLE);
            surface_sv_local.setVisibility(View.INVISIBLE);

            if(audioManager!=null) {
                Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                audioManager.setMode(AudioManager.MODE_RINGTONE);
                audioManager.setSpeakerphoneOn(true);
                ringtone = RingtoneManager.getRingtone(this, ringUri);
                ringtone.play();
            }
            EMClient.getInstance().callManager().setSurfaceView(surface_sv_local, surface_sv_opposite);
        }

        final int MAKE_CALL_TIMEOUT = 50 * 1000;
        handler.removeCallbacks(timeoutHangup);
        handler.postDelayed(timeoutHangup, MAKE_CALL_TIMEOUT);

        // get instance of call helper, should be called after setSurfaceView was called
        callHelper = EMClient.getInstance().callManager().getVideoCallHelper();
    }


    /**
     * set call state listener
     */
    void addCallStateListener() {
        callStateListener = new EMCallStateChangeListener() {

            @Override
            public void onCallStateChanged(final CallState callState, final CallError error) {
                switch (callState) {

                case CONNECTING: // is connecting
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            call_tv_state.setText(R.string.Are_connected_to_each_other);
                        }

                    });
                    break;
                case CONNECTED: // connected
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
//                            callStateTextView.setText(R.string.have_connected_with);
                        }

                    });
                    break;

                case ACCEPTED: // call is accepted
                    surface_state = 0;
                    handler.removeCallbacks(timeoutHangup);

                    //推流时设置水印图片
                    if(PreferenceManager.getInstance().isWatermarkResolution()){
                        EMClient.getInstance().callManager().setWaterMark(watermark);
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (soundPool != null)
                                    soundPool.stop(streamID);
                                EMLog.d("EMCallManager", "soundPool stop ACCEPTED");
                            } catch (Exception e) {
                            }
                            openSpeakerOn();
                            call_tv_is_p2p.setText(EMClient.getInstance().callManager().isDirectCall() ? R.string.direct_call : R.string.relay_call);
                            iv_handsfree.setImageResource(R.drawable.em_icon_speaker_on);
                            isHandsfreeState = true;
                            isInCalling = true;
                            call_chm_tick.setVisibility(View.VISIBLE);
                            call_chm_tick.setBase(SystemClock.elapsedRealtime());
                            // call durations start
                            call_chm_tick.start();
                            call_tv_nickname.setVisibility(View.INVISIBLE);
                            call_tv_state.setText(R.string.In_the_call);
//                            recordBtn.setVisibility(View.VISIBLE);
                            callingState = CallingState.NORMAL;
                            startMonitor();
                            // Start to watch the phone call state.
                            PhoneStateManager.get(VideoCallActivity.this).addStateCallback(phoneStateCallback);
                        }

                    });
                    break;
                case NETWORK_DISCONNECTED:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //call_tv_networkstatus.setVisibility(View.VISIBLE);
                            //call_tv_networkstatus.setText(R.string.network_unavailable);
                        }
                    });
                    break;
                case NETWORK_UNSTABLE:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            call_tv_networkstatus.setVisibility(View.VISIBLE);
                            if(error == CallError.ERROR_NO_DATA){
                                call_tv_networkstatus.setText(R.string.no_call_data);
                            }else{
                                call_tv_networkstatus.setText(R.string.network_unstable);
                            }
                        }
                    });
                    break;
                case NETWORK_NORMAL:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            call_tv_networkstatus.setVisibility(View.INVISIBLE);
                        }
                    });
                    break;
                case VIDEO_PAUSE:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VIDEO_PAUSE", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case VIDEO_RESUME:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VIDEO_RESUME", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case VOICE_PAUSE:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VOICE_PAUSE", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case VOICE_RESUME:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VOICE_RESUME", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case DISCONNECTED: // call is disconnected
                    handler.removeCallbacks(timeoutHangup);
                    @SuppressWarnings("UnnecessaryLocalVariable")final CallError fError = error;
                    runOnUiThread(new Runnable() {
                        private void postDelayedCloseMsg() {
                            uiHandler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    removeCallStateListener();

                                    // Stop to watch the phone call state.
                                    PhoneStateManager.get(VideoCallActivity.this).removeStateCallback(phoneStateCallback);

                                    saveCallRecord();
                                    if(isInCalling){
                                        if(customMsg_Type.equals("buyinfo")){
                                            call_btn_hangup.setVisibility(View.GONE);
                                            call_coming_ll.setVisibility(View.GONE);
                                            buyinfo_ll_btns.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            finish();
                                        }
                                    }
                                    else {
                                        finish();
                                    }
                                }

                            }, 200);
                        }

                        @Override
                        public void run() {
                            call_chm_tick.stop();
                            callDruationText = call_chm_tick.getText().toString();
                            String s1 = getResources().getString(R.string.The_other_party_refused_to_accept);
                            String s2 = getResources().getString(R.string.Connection_failure);
                            String s3 = getResources().getString(R.string.The_other_party_is_not_online);
                            String s4 = getResources().getString(R.string.The_other_is_on_the_phone_please);
                            String s5 = getResources().getString(R.string.The_other_party_did_not_answer);

                            String s6 = getResources().getString(R.string.hang_up);
                            String s7 = getResources().getString(R.string.The_other_is_hang_up);
                            String s8 = getResources().getString(R.string.did_not_answer);
                            String s9 = getResources().getString(R.string.Has_been_cancelled);
                            String s10 = getResources().getString(R.string.Refused);
                            String st12 = "service not enable";
                            String st13 = "service arrearages";
                            String st14 = "service forbidden";

                            if (fError == CallError.REJECTED) {
                                callingState = CallingState.BEREFUSED;
                                call_tv_state.setText(s1);
                            } else if (fError == CallError.ERROR_TRANSPORT) {
                                call_tv_state.setText(s2);
                            } else if (fError == CallError.ERROR_UNAVAILABLE) {
                                callingState = CallingState.OFFLINE;
                                call_tv_state.setText(s3);
                            } else if (fError == CallError.ERROR_BUSY) {
                                callingState = CallingState.BUSY;
                                call_tv_state.setText(s4);
                            } else if (fError == CallError.ERROR_NORESPONSE) {
                                callingState = CallingState.NO_RESPONSE;
                                call_tv_state.setText(s5);
                            }else if (fError == CallError.ERROR_LOCAL_SDK_VERSION_OUTDATED || fError == CallError.ERROR_REMOTE_SDK_VERSION_OUTDATED){
                                callingState = CallingState.VERSION_NOT_SAME;
                                call_tv_state.setText(R.string.call_version_inconsistent);
                            } else if(fError == CallError.ERROR_SERVICE_NOT_ENABLE) {
                                callingState = CallingState.SERVICE_NOT_ENABLE;
                                call_tv_state.setText(st12);
                            } else if(fError == CallError.ERROR_SERVICE_ARREARAGES) {
                                callingState = CallingState.SERVICE_ARREARAGES;
                                call_tv_state.setText(st13);
                            } else if(fError == CallError.ERROR_SERVICE_FORBIDDEN) {
                                callingState = CallingState.SERVICE_NOT_ENABLE;
                                call_tv_state.setText(st14);
                            } else {
                                if (isRefused) {
                                    callingState = CallingState.REFUSED;
                                    call_tv_state.setText(s10);
                                }
                                else if (isAnswered) {
                                    callingState = CallingState.NORMAL;
                                    if (isEndCallTriggerByMe) {
//                                        callStateTextView.setText(s6);
                                    } else {
                                        call_tv_state.setText(s7);
                                    }
                                } else {
                                    if (isInComingCall) {
                                        callingState = CallingState.UNANSWERED;
                                        call_tv_state.setText(s8);
                                    } else {
                                        if (callingState != CallingState.NORMAL) {
                                            callingState = CallingState.CANCELLED;
                                            call_tv_state.setText(s9);
                                        } else {
                                            call_tv_state.setText(s6);
                                        }
                                    }
                                }
                            }
                            Toast.makeText(VideoCallActivity.this, call_tv_state.getText(), Toast.LENGTH_SHORT).show();
                            postDelayedCloseMsg();
                        }
                    });
                    break;
                default:
                    break;
                }
            }
        };
        EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
    }

    void removeCallStateListener() {
        EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
    }

    PhoneStateManager.PhoneStateCallback phoneStateCallback = new PhoneStateManager.PhoneStateCallback() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:   // 电话响铃
                    break;
                case TelephonyManager.CALL_STATE_IDLE:      // 电话挂断
                    // resume current voice conference.
                    if (isMuteState) {
                        try {
                            EMClient.getInstance().callManager().resumeVoiceTransfer();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                        try {
                            EMClient.getInstance().callManager().resumeVideoTransfer();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:   // 来电接通 或者 去电，去电接通  但是没法区分
                    // pause current voice conference.
                    if (!isMuteState) {
                        try {
                            EMClient.getInstance().callManager().pauseVoiceTransfer();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                        try {
                            EMClient.getInstance().callManager().pauseVideoTransfer();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_coming_btn_refusecall: // decline the call
                isRefused = true;
                call_coming_btn_refusecall.setEnabled(false);
                handler.sendEmptyMessage(MSG_CALL_REJECT);
                break;
            case R.id.call_coming_btn_answercall: // answer the call
                EMLog.d(TAG, "btn_answer_call clicked");
                call_coming_btn_answercall.setEnabled(false);
                openSpeakerOn();
                if (ringtone != null)
                    ringtone.stop();

                call_tv_state.setText("answering...");
                handler.sendEmptyMessage(MSG_CALL_ANSWER);
                iv_handsfree.setImageResource(R.drawable.em_icon_speaker_on);
                isAnswered = true;
                isHandsfreeState = true;

                call_coming_ll.setVisibility(View.INVISIBLE);
                call_btn_hangup.setVisibility(View.VISIBLE);
                voice_ll.setVisibility(View.VISIBLE);
                surface_sv_local.setVisibility(View.VISIBLE);
                break;

            case R.id.call_btn_hangup: // hangup
                call_btn_hangup.setEnabled(false);
                call_chm_tick.stop();
                isEndCallTriggerByMe = true;
                call_tv_state.setText(getResources().getString(R.string.hanging_up));
                EMLog.d(TAG, "btn_hangup_call");
                handler.sendEmptyMessage(MSG_CALL_END);

//                if(customMsg_BuyInfo==null){
//                    customMsg_BuyInfo=new CustomMsg<>();
//                    customMsg_BuyInfo.setType("buyinfo");
//
//                    MsgContentByBuyInfo buyInfo=new MsgContentByBuyInfo();
//                    buyInfo.setMachineId("112122313");
//                    buyInfo.setStoreName("店铺名称");
//                    buyInfo.setHandleStatus(1);
//                    buyInfo.setHandleDescribe("同意");
//                    buyInfo.setOperateUserName("chingment");
//
//                    customMsg_BuyInfo.setContent(buyInfo);
//                }
//
//                EMMessage message3 = createCustomSendMessage(customMsg_BuyInfo, username);
//                EMClient.getInstance().chatManager().sendMessage(message3);


                break;
            case R.id.iv_mute: // mute
                if (isMuteState) {
                    // resume voice transfer
                    iv_mute.setImageResource(R.drawable.em_icon_mute_normal);
                    try {
                        EMClient.getInstance().callManager().resumeVoiceTransfer();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    isMuteState = false;
                } else {
                    // pause voice transfer
                    iv_mute.setImageResource(R.drawable.em_icon_mute_on);
                    try {
                        EMClient.getInstance().callManager().pauseVoiceTransfer();
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                    isMuteState = true;
                }
                break;
            case R.id.iv_handsfree: // handsfree
                if (isHandsfreeState) {
                    // turn off speaker
                    iv_handsfree.setImageResource(R.drawable.em_icon_speaker_normal);
                    closeSpeakerOn();
                    isHandsfreeState = false;
                } else {
                    iv_handsfree.setImageResource(R.drawable.em_icon_speaker_on);
                    openSpeakerOn();
                    isHandsfreeState = true;
                }
                break;
            case R.id.iv_switchcamera: //switch camera
                handler.sendEmptyMessage(MSG_CALL_SWITCH_CAMERA);
                break;
            case R.id.buyinfo_btn_tongyi:

                new EaseAlertDialog(VideoCallActivity.this,null, "确定同意？", null,new EaseAlertDialog.AlertDialogUser() {

                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if(confirmed){
                            if(customMsg_BuyInfo!=null) {
                                customMsg_BuyInfo.getContent().setHandleStatus(1);
                                customMsg_BuyInfo.getContent().setHandleDescribe("同意");
                                sendCustomMessage(customMsg_BuyInfo, username);
                            }
                            finish();
                        }
                    }
                }, true).show();


                break;
            case R.id.buyinfo_btn_butongyi:

                new EaseAlertDialog(VideoCallActivity.this,null, "确定不同意？", null,new EaseAlertDialog.AlertDialogUser() {

                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if(confirmed){
                            if(customMsg_BuyInfo!=null) {
                                customMsg_BuyInfo.getContent().setHandleStatus(2);
                                customMsg_BuyInfo.getContent().setHandleDescribe("不同意");
                                sendCustomMessage(customMsg_BuyInfo, username);
                            }
                            finish();
                        }
                    }
                }, true).show();

                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        DemoHelper.getInstance().isVideoCalling = false;
        stopMonitor();
        surface_sv_local.getRenderer().dispose();
        surface_sv_local = null;
        surface_sv_opposite.getRenderer().dispose();
        surface_sv_opposite = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        callDruationText = call_chm_tick.getText().toString();
        super.onBackPressed();
    }

    /**
     * for debug & testing, you can remove this when release
     */
    void startMonitor(){
        isMonitor = true;
        EMCallSession callSession = EMClient.getInstance().callManager().getCurrentCallSession();
        final boolean isRecord = callSession.isRecordOnServer();
        final String serverRecordId = callSession.getServerRecordId();

        EMLog.e(TAG, "server record: " + isRecord);
        if (isRecord) {
            EMLog.e(TAG, "server record id: " + serverRecordId);
        }
        final String recordString = " record? " + isRecord + " id: " + serverRecordId;
        new Thread(new Runnable() {
            public void run() {
                while(isMonitor){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            call_tv_monitor.setText("WidthxHeight："+callHelper.getVideoWidth()+"x"+callHelper.getVideoHeight()
                                    + "\nDelay：" + callHelper.getVideoLatency()
                                    + "\nFramerate：" + callHelper.getVideoFrameRate()
                                    + "\nLost：" + callHelper.getVideoLostRate()
                                    + "\nLocalBitrate：" + callHelper.getLocalBitrate()
                                    + "\nRemoteBitrate：" + callHelper.getRemoteBitrate()
                                    + "\n" + recordString);

                            call_tv_is_p2p.setText(EMClient.getInstance().callManager().isDirectCall()
                                    ? R.string.direct_call : R.string.relay_call);
                        }
                    });
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }, "CallMonitor").start();
    }

    void stopMonitor(){
        isMonitor = false;
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if(isInCalling){
            try {
                EMClient.getInstance().callManager().pauseVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isInCalling){
            try {
                EMClient.getInstance().callManager().resumeVideoTransfer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    //todo 本地->测试自定义格式
    public void sendCustomMessage(CustomMsg<MsgContentByBuyInfo>  var0, String var1) {
        if (var0!=null) {

            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.CUSTOM);

            EMCustomMessageBody var3 = new EMCustomMessageBody(JSON.toJSONString(var0));

            var3.setEvent(var0.getType());

            HashMap<String,String> params=new HashMap<>();

            params.put("type",var0.getType());
            params.put("content",JSON.toJSONString(var0.getContent()));

            var3.setParams(params);

            message.addBody(var3);
            message.setTo(var1);

            EMClient.getInstance().chatManager().sendMessage(message);
        }
    }


}
