package com.srtc.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.feinno.srtclib_android.FeinnoMegLibSDK;
import com.feinno.srtclib_android.bean.JniObjs;
import com.feinno.srtclib_android.util.LocalConstans;
import com.feinno.xmcallengine.FeinnoVideoEngine;
import com.srtc.R;
import com.srtc.dialog.ExitRoomDialog;
import com.srtc.utils.EnterUserInfo;
import com.srtc.utils.ToastUtil;
import com.wushuangtech.library.Constants;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Title:VideoActivity
 * <p>
 * Description:
 * </p>
 * Author Han.C
 * Date 2020/2/21 8:16 PM
 */
public class VideoActivity extends BaseActivity {



    private static final String TAG = VideoActivity.class.getSimpleName();
    @BindView(R.id.remote1)
    ConstraintLayout remote1;
    @BindView(R.id.remote2)
    ConstraintLayout remote2;
    @BindView(R.id.remote3)
    ConstraintLayout remote3;
    @BindView(R.id.remote4)
    ConstraintLayout remote4;
    @BindView(R.id.remote5)
    ConstraintLayout remote5;
    @BindView(R.id.remote6)
    ConstraintLayout remote6;
    @BindView(R.id.close)
    ImageView close;
    @BindView(R.id.remote7)
    ConstraintLayout remote7;
    @BindView(R.id.remote8)
    ConstraintLayout remote8;
    @BindView(R.id.remote9)
    ConstraintLayout remote9;
    private String uccId;
    private String roomId;
    private List<ConstraintLayout> mRemoteList = new ArrayList<>();
    private List<String> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference_video);
        ButterKnife.bind(this);
        uccId = getIntent().getStringExtra("USER_ID");
        roomId = getIntent().getStringExtra("ROOM_ID");

        initView();
        initBroadCast();
        userList = new ArrayList<>();
        //显示并上传本地视频流数据
        SurfaceView mSurfaceView = FeinnoMegLibSDK.getInstance().createRendererView(this);
        FeinnoVideoEngine.getInstance().setupLocalVideo(Long.parseLong(uccId), mSurfaceView, getRequestedOrientation());
        remote1.addView(mSurfaceView);//因为三体的角色系统和SRTC平台不一样，这里我的视频信息始终放在第一位

        initListener();
        initDialog();

    }

    private ExitRoomDialog mExitRoomDialog;

    private void initDialog() {
        mExitRoomDialog = new ExitRoomDialog(mContext, R.style.NoBackGroundDialog);
        mExitRoomDialog.setCanceledOnTouchOutside(false);
        mExitRoomDialog.mConfirmBT.setOnClickListener(v -> {
            exitRoom();
            mExitRoomDialog.dismiss();
        });
        mExitRoomDialog.mDenyBT.setOnClickListener(v -> mExitRoomDialog.dismiss());

    }

    public void exitRoom() {
        FeinnoMegLibSDK.getInstance().leaveChannel();
        finish();
    }


    private SurfaceView mUserSurfaceView;

    /**
     * 显示其他人的信息
     *
     * @param jniObjs
     */
    private void addUser(JniObjs jniObjs) {
        for (int i = 0; i < mRemoteList.size(); i++) {
            ConstraintLayout remote = mRemoteList.get(i);
            if (remote.getChildCount() == 0) {
                mUserSurfaceView = FeinnoMegLibSDK.getInstance().createRendererView(this);
                mUserSurfaceView.setZOrderMediaOverlay(true);
                FeinnoMegLibSDK.getInstance().setupRemoteVideo(mUserSurfaceView, String.valueOf(jniObjs.mUid));
                remote.addView(mUserSurfaceView);
                remote.setTag(jniObjs.mUid);

//                if (TextUtils.equals(role, "1")) {
//                    //上报Sei位置信息，用于视频留存参与者位置
//                    FeinnoVideoCompositingLayout layout = new FeinnoVideoCompositingLayout();
//                    layout.regions = buildRemoteLayoutLocation(Long.parseLong(uccId), jniObjs.mUid, remote);
//                    int result = FeinnoMegLibSDK.getInstance().setVideoCompositingLayout(layout);
//                    LogFeinno.e(TAG, "result = " + result);
//                }

                return;
            }

        }
    }

    private void initView() {
        mRemoteList.add(remote1);
        mRemoteList.add(remote2);
        mRemoteList.add(remote3);
        mRemoteList.add(remote4);
        mRemoteList.add(remote5);
        mRemoteList.add(remote6);
        mRemoteList.add(remote7);
        mRemoteList.add(remote8);
        mRemoteList.add(remote9);

    }

    private void initListener() {

        //挂断
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExitRoomDialog.show();
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mLocalBroadcast);
        super.onDestroy();
    }

    private MyLocalBroadcastReceiver mLocalBroadcast;

    private void initBroadCast() {
        mLocalBroadcast = new MyLocalBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocalConstans.SEND_TAG);
        filter.addAction(LocalConstans.INVITE_A);
        filter.addAction(LocalConstans.REJECT_A);
        filter.addAction(LocalConstans.RING_A);
        filter.addAction(LocalConstans.LEAVEL_L);
        filter.addAction(LocalConstans.PUSHLISH_MSG);
        filter.addCategory("ttt.test.interface");
        filter.addAction("ttt.test.interface.string");
        registerReceiver(mLocalBroadcast, filter);

        //设置广播,接收现在已经在房间中的其他人信息
        FeinnoMegLibSDK.getInstance().setCallBackFalse();
    }


    private class MyLocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (LocalConstans.SEND_TAG.equals(action)) {
                JniObjs mJniObjs = intent.getParcelableExtra(LocalConstans.MSG_TAG);
                switch (mJniObjs.mJniType) {
                    case LocalConstans.CALL_BACK_ON_VIDEO_QUARY_BAD:
                        //todo-对方视频质量不佳（对方视频流上传异常）--------------
                        break;
                    case LocalConstans.CALL_BACK_ON_VIDEO_QUARY_WELL:
                        //todo-对方视频质量恢复（视频会话正常过程中，该回调会一直被触发）
                        //todo-当对方视频质量从不佳-->到恢复，可以根据该回调处理一些UI层面的东西
                        break;
                    case LocalConstans.CALL_BACK_ON_ERROR:

                        break;
                    case LocalConstans.CALL_BACK_ON_CONNECTLOST:
//                        mErrorExitDialog.setMessage("退出原因: 房间网络断开");//设置显示的内容
//                        mErrorExitDialog.show();
                        break;
                    case LocalConstans.CALL_BACK_ON_USER_KICK:
                        //被房主请出房间
                        int reason = mJniObjs.mReason;
                        if (reason == Constants.ERROR_KICK_BY_HOST) {
                            ToastUtil.showShort(VideoActivity.this, "您已被房主踢出房间");
                        } else if (reason == Constants.ERROR_KICK_BY_NEWCHAIRENTER) {
                            ToastUtil.showShort(VideoActivity.this, "其他人以主播身份进入了房间");
                        }
                        FeinnoMegLibSDK.getInstance().leaveConference(roomId, uccId);
                        VideoActivity.this.finish();
                        break;
                    case LocalConstans.CALL_BACK_ON_USER_JOIN:
//                        if (mp != null && mp.isPlaying()) {
//                            mp.stop();
//                            mp.release();
//                            mp = null;
//                            LogFeinno.e("======", "结束振铃");
//                        }
//                        long uid = mJniObjs.mUid;
//                        EnterUserInfo userInfo = new EnterUserInfo(uid);
//                        mRemoteManager.addAndSendSei(Long.parseLong(mUserId), userInfo);
                        userList.add(String.valueOf(mJniObjs.mUid));
                        addUser(mJniObjs);
                        break;
                    case LocalConstans.CALL_BACK_ON_USERENABLEVIDEO:
                        long uids = mJniObjs.mUid;
                        EnterUserInfo userInfos = new EnterUserInfo(uids);
//                        mRemoteManager.addAndSendSei(Long.parseLong(mUserId), userInfos);
                        break;
                    case LocalConstans.CALL_BACK_ON_USER_OFFLINE:

                        long offLineUserID = mJniObjs.mUid;
                        for (int i = 0; i < mRemoteList.size(); i++) {
                            if (mRemoteList.get(i).getTag() != null
                                    && (Long) mRemoteList.get(i).getTag() == offLineUserID) {
                                mRemoteList.get(i).removeAllViews();
                            }
                        }
                        userList.remove(String.valueOf(mJniObjs.mUid));
                        break;
                    case LocalConstans.CALL_BACK_ON_MUTE_AUDIO:
                        break;
                    case LocalConstans.CALL_BACK_ON_SEI:
                        break;
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        mExitRoomDialog.show();
    }
}
