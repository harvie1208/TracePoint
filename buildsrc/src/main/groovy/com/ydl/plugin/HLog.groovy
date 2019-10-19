package com.ydl.plugin

/**
 * 日志打印
 */

class HLog{

    public static String TAG = "[NOTracePoint]"

    public static void e(String str){
        if (ClassModifyUtil.noTracePointPluginParams.isPrintLog){
            println(TAG+ "error========="+str)
        }
    }
    public static void i(String str){
        if (ClassModifyUtil.noTracePointPluginParams.isPrintLog){
            println(TAG+ "info========="+str)
        }
    }

}