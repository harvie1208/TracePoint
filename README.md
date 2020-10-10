# TracePoint
android 无埋点数据采集gradle插件（ASM字节码插桩）

掘金原理篇：https://juejin.im/post/5dae95c4f265da5bb7466357

#### 更新记录：
##### 0.2.0
```
1.去除v4、v7包支持
2.支持androidx

基于tools.build:gradle:4.0.1, gradle:6.6, sdkVersion:29 开发测试

配置变动点：
1.需要在gradle.properties中增加 android.enableD8.desugaring=false ，否则对lambda表达式不起作用
2.事件接收类中activity和fragment导入类改为androidx包名下的，可参考demo app模块代码
```

### 接入流程：

#### 1.在项目根目录build.gradle中引入插件
```
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.0'
        classpath 'com.harvie.plugin:traceplugin:0.0.1' //无埋点采集插件
    }
}
```
#### 2.在app模块build.gradle中应用插件及配置
```
apply plugin: 'com.android.application'
apply plugin: 'trace-point' //无埋点插件应用

...省略常规项

//无埋点相关配置
noTracePoint{
    pluginName = "tracepoint" //插件名称随意命名
    //接收事件处理类（插件会将以下自定义类的相关方法插入目标方法中）
    targetClassName = 'com.example.tracepointplugin.InterceptEventHanlder'
    //开启会将修改后的文件copy一份到app/build/trace-point目录下，用于查看修改结果
    outputModifyFile = true 
    //目标包名 数组类型（一般场景写业务模块包名即可）
    targetPackages = ['com.example']
}

```
#### 3.编写接收事件类

类名和包名可以自定义(在2步中配置)，但类方法名及形参必须如下一致，否则可能出异常
```
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
          //以下ViewPath工具类可从源码app模块中获取
            Activity activity = VIewPathUtil.getActivity(view);
            String path = VIewPathUtil.getViewPath(activity,view);

            Log.e(TAG,"viewPath:"+path);
        }catch (Exception e){e.printStackTrace();}
    }
}
```
#### 4.运行

到此就已经集成好了

build一下就会在app/build/trace-point文件中看到修改后的代码

run一下，浏览页面或是点击按钮时就会看到相关log输出


欢迎star 下期继续优化
