package com.srtc.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.srtc.R;
import com.srtc.utils.CheckSumBuilder;
import com.srtc.utils.ToastUtil;
import com.yanzhenjie.permission.AndPermission;

import java.nio.Buffer;
import java.util.Random;

public class SplashActivity extends BaseActivity {
    private ProgressDialog mDialog;
    private EditText mRoomIDET;
    private String mRoomName;
    private long mUserId;
    private TextView mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        // 权限申请
        AndPermission.with(this)
                .permission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)
                .start();
        mRoomIDET = findViewById(R.id.room_id);
        mDialog = new ProgressDialog(this);
        mDialog.setTitle("");
        mDialog.setMessage("SDK登录中...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        FeinnoMegLibSDK.getInstance().setCallBack(new SrtcCallBackListener() {
            @Override
            public void onLoginStatus(int i, String s) {
                if (i == 0) {
                    mDialog.setMessage("正在进入房间...");
                    FeinnoMegLibSDK.getInstance().joinChannel(String.valueOf(mUserId), mRoomName, "",
                            "2", "2", "6");
                } else {
                    if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
                    ToastUtil.showShort(SplashActivity.this, "登录失败 code=" + i + "    msg=" + s);
                }
            }

            @Override
            public void onError(int i) {
                ToastUtil.showShort(SplashActivity.this, "进入房间失败 i = "+i);
                if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
            }

            @Override
            public void onJoinChannelSuccess(String s, long l, String s1) {
                if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
                Intent activityIntent = new Intent();
                activityIntent.putExtra("ROOM_ID", s);
                activityIntent.putExtra("USER_ID", String.valueOf(l));
                activityIntent.setClass(SplashActivity.this, VideoActivity.class);
                startActivity(activityIntent);
            }

            @Override
            public void onUserJoined(long l, int i) {

            }

            @Override
            public void onUserEnableVideo(long l, boolean b) {

            }

            @Override
            public void onLeaveChannel() {

            }

            @Override
            public void onFirstLocalVideoFrame(int i, int i1) {

            }

            @Override
            public void onFirstRemoteVideoFrame(long l, int i, int i1) {

            }

            @Override
            public void onFirstRemoteVideoDecoded(long l, int i, int i1) {

            }

            @Override
            public void onUserOffline(long l, int i) {

            }

            @Override
            public void onCameraReady() {

            }

            @Override
            public void onAudioVolumeIndication(long l, int i, int i1) {

            }

            @Override
            public void onRtcStats() {

            }

            @Override
            public void onAudioRouteChanged(int i) {

            }

            @Override
            public void onSetSEI(String s) {

            }

            @Override
            public void onVideoStopped() {

            }

            @Override
            public void onPlayChatAudioCompletion(String s) {

            }

            @Override
            public void onRequestChannelKey() {

            }

            @Override
            public void onUserRoleChanged(long l, int i) {

            }

            @Override
            public void onScreenRecordTime(int i) {

            }

            @Override
            public void onUserMuteAudio(long l, boolean b) {

            }

            @Override
            public void onStatusOfRtmpPublish(int i, String s) {

            }

            @Override
            public void onStatusOfPull(int i, String s) {

            }

            @Override
            public void onAudienceNumber(long l) {

            }

            @Override
            public void onFeverNumber(long l) {

            }

            @Override
            public void onSpeakingMuted(long l, boolean b) {

            }

            @Override
            public void onLocalVideoFrameCapturedBytes(int i, int i1, Buffer buffer) {

            }

            @Override
            public void onRemoteVideoFrameDecodedOfUid(int i, int i1, Buffer buffer) {

            }

            @Override
            public void onRtmpNumber(int i) {

            }

            @Override
            public void onDanmukuContent(DanmukuBean danmukuBean) {

            }

            @Override
            public void onNetChanged(int i) {

            }

            @Override
            public String getAuthParams(RtmpUrlBean rtmpUrlBean) {
                return null;
            }

            @Override
            public void FMLogCallBack(String s) {

            }

            @Override
            public void connectMqttCallBack(int i, String s) {

            }

            @Override
            public void onOpeTime(String s) {

            }
        });
    }


    public void onClickEnterButton(View v) {
        mRoomName = mRoomIDET.getText().toString().trim();
        if (TextUtils.isEmpty(mRoomName)) {
            Toast.makeText(this, "房间ID不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        mDialog.setMessage("SDK登陆中...");
        mDialog.show();
        FeinnoMegLibSDK.getInstance().setServiceIp("");
        Random mRandom = new Random();
        mUserId = mRandom.nextInt(999999);
        String curtime = String.valueOf(System.currentTimeMillis());
        //todo--实际业务处理中，不用每次加入房间都登陆，登陆成功一次即可。
        FeinnoMegLibSDK.getInstance().loginSDK("",
                String.valueOf(mUserId), curtime,
                CheckSumBuilder.getCheckSum("", String.valueOf(mUserId), curtime));

    }




}
