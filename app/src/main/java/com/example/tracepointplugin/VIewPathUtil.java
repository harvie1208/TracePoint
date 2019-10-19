package com.example.tracepointplugin;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * view唯一ID生成器
 * @author harvie
 * @date 2019/8/30
 */
class VIewPathUtil {

    /**
     * 获取view的页面唯一值
     * @return
     */
    public static String getViewPath(Activity activity, View view){
        String pageName = activity.getClass().getName();
        String vId = getViewId(view);
        return pageName+"_"+MD5Util.md5(vId);
    }

    /**
     * 获取页面名称
     * @param view
     * @return
     */
    public static Activity getActivity(View view){
        Context context = view.getContext();
        while (context instanceof ContextWrapper){
            if (context instanceof Activity){
                return ((Activity)context);
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /**
     * 获取view唯一id,根据xml文件内容计算
     * @param view1
     * @return
     */
    private static String getViewId(View view1){

        StringBuilder sb = new StringBuilder();

        //当前需要计算位置的view
        View view = view1;
        ViewParent viewParent =  view.getParent();

        while (viewParent!=null && viewParent instanceof ViewGroup){
            ViewGroup tview = (ViewGroup) viewParent;
            int index = getChildIndex(tview,view);
            sb.insert(0,view.getClass().getSimpleName()+"["+(index==-1?"-":index)+"]");
            viewParent = tview.getParent();
            view = tview;
        }
        return sb.toString();
    }

    /**
     * 计算当前 view在父容器中相对于同类型view的位置
     */
    private static int getChildIndex(ViewGroup viewGroup, View view){
        if (viewGroup ==null || view == null){
            return -1;
        }
        String viewName = view.getClass().getName();
        int index = 0;
        for (int i = 0;i < viewGroup.getChildCount();i++){
            View el = viewGroup.getChildAt(i);
            String elName = el.getClass().getName();
            if (elName.equals(viewName)){
                //表示同类型的view
                if (el == view){
                    return index;
                }else {
                    index++;
                }
            }
        }
        return -1;
    }
}
