package org.xuxiaoxiao.xiao.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.model.WilddogUser;
import com.wilddog.wilddogcore.WilddogApp;

import org.xuxiaoxiao.xiao.infrastructure.SoundPool;
import org.xuxiaoxiao.xiao.infrastructure.User;
import org.xuxiaoxiao.xiao.infrastructure.XiaoApplication;

/**
 * Created by WuQiang on 2017/4/10.
 */

public class BaseFragment extends Fragment {

    protected XiaoApplication application;
    protected User user;
    protected SoundPool soundPool;
//    protected SyncReference mWilddogRef;
    WilddogApp wilddogApp;
    protected WilddogAuth wilddogAuth;
   protected WilddogUser wUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (XiaoApplication)getActivity().getApplication();
        user = application.getUser();
        soundPool = application.getSoundPool();
        // Setup our Wilddog mWilddogRef
//        mWilddogRef = WilddogSync.getInstance().getReference().child("chat");
        this.wUser = application.getwUser();
        wilddogAuth = application.getWilddogAuth();
    }

}
