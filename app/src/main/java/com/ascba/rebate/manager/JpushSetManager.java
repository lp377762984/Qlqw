package com.ascba.rebate.manager;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;

import com.ascba.rebate.appconfig.AppConfig;
import com.ascba.rebate.application.MyApplication;
import com.yanzhenjie.nohttp.tools.NetUtils;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by 李平 on 2017/7/17.
 * 一个极光推送的管理类
 */

public class JpushSetManager {
    private static final String TAG="JpushSetManager";
    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    JPushInterface.setAliasAndTags(MyApplication.getInstance(), (String) msg.obj, null, mAliasCallback);
                    break;
                case MSG_SET_TAGS:
                    JPushInterface.setAliasAndTags(MyApplication.getInstance(), null, (Set<String>) msg.obj, mTagsCallback);
                    break;
                default:
                    break;
            }
        }
    };
    private int type;//0设置成功 1清除成功

    public JpushSetManager(int type) {
        this.type=type;
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            switch (code) {
                case 0://成功
                    Log.d(TAG, "gotResult: setAliasSuccess,"+alias);
                    if(type==0){
                        AppConfig.getInstance().putBoolean("jpush_set_tag_success",true);
                    }else if(type==1){
                        AppConfig.getInstance().putBoolean("jpush_set_tag_success",false);
                    }
                    break;
                case 6002://失败，重试
                    if (NetUtils.isMobileConnected()) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.e(TAG, "网络异常" );
                    }
                    break;
                default:
            }
        }
    };
    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            switch (code) {
                case 0:
                    Log.d(TAG, "gotResult: setTagsSuccess");
                    if(type==0){
                        AppConfig.getInstance().putBoolean("jpush_set_alias_success",true);
                    }else if(type==1){
                        AppConfig.getInstance().putBoolean("jpush_set_alias_success",false);
                    }
                    break;
                case 6002:
                    if (NetUtils.isMobileConnected()) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.e(TAG, "网络异常" );
                    }
                    break;

                default:
            }
        }

    };
    //设置标签
    public void setTag(Set<String> tagSet) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, tagSet));
    }
    //设置别名
    public void setAlias(String alias) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }
}
