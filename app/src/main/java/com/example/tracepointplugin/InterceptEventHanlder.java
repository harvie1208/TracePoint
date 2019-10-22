package com.example.tracepointplugin;

import android.app.Activity;
import android.app.Fragment;
import android.util.Log;
import android.view.View;

/**
 * 拦截事件处理
 */
class InterceptEventHanlder{

    private static String TAG = "tracepoint";

    //------------------- activity 事件接收

    public static void activityOnCreate(Activity activity){
        Log.e(TAG,activity.getClass().getName());
    }

    public static void activityOnResume(Activity activity) {
        Log.e(TAG,activity.getClass().getName());
    }

    //------------------- fragment 事件接收

    public static void setUserVisibleHint(Fragment fragment, boolean visiable){
        if (visiable){
            Log.e(TAG,"pv:"+fragment.getClass().getName());
        }
    }

    public static void onHiddenChanged(Fragment fragment,boolean hidden){

        if (!hidden){
            Log.e(TAG,"pv:"+fragment.getClass().getName());
        }
    }

    public static void setUserVisibleHint(android.support.v4.app.Fragment fragment,boolean visiable){

        if (visiable){
            Log.e(TAG,"pv:"+fragment.getClass().getName());
        }
    }

    public static void onHiddenChanged(android.support.v4.app.Fragment fragment,boolean hidden){

        if (!hidden){
            Log.e(TAG,"pv:"+fragment.getClass().getName());
        }
    }

    //------------------- click 事件接收

    public static void onClick(View view){

        try {
            Activity activity = VIewPathUtil.getActivity(view);
            String path = VIewPathUtil.getViewPath(activity,view);

            Log.e(TAG,"viewPath:"+path);
        }catch (Exception e){e.printStackTrace();}
    }
}