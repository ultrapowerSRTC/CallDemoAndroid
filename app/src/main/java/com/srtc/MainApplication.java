package com.srtc;

import android.app.Application;
import com.feinno.srtclib_android.FeinnoMegLibSDK;

public class MainApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        FeinnoMegLibSDK.init(this);
    }

}
