package com.harvie.plugin.traceplugin

import org.objectweb.asm.Opcodes

class InterceptEventConfig{

    public static String owner //接收事件方法的类名 app 模块下build.gradle 中配置

    //点击事件方法
    public static final HashMap<String, MethodCode> interfaceMethods = new HashMap<>()
    //fragment中方法
    public static final HashMap<String, MethodCode> fragmentMethods = new HashMap<>()
    //activity方法
    public static final HashMap<String, MethodCode> activityMethods = new HashMap<>()

    static {
        interfaceMethods.put("onClick(Landroid/view/View;)V", new MethodCode(
                "onClick",
                "(Landroid/view/View;)V",
                "onClick",
                "(Landroid/view/View;)V",
                "",
                1,
                2,
                [Opcodes.ALOAD],
        ))

    }

    static {
        //viewpage
        fragmentMethods.put('android/app/Fragment$setUserVisibleHint(Z)V',new MethodCode(
                'setUserVisibleHint',
                '(Z)V',
                'setUserVisibleHint',
                '(Landroid/app/Fragment;Z)V',
                '',//parent省略
                0,
                2,
                [Opcodes.ALOAD,Opcodes.IALOAD]
        ))

        //FragmentTransaction方式
        fragmentMethods.put('android/app/Fragment$onHiddenChanged(Z)V',new MethodCode(
                'onHiddenChanged',
                '(Z)V',
                'onHiddenChanged',
                '(Landroid/app/Fragment;Z)V',
                '',//parent省略
                0,
                2,
                [Opcodes.ALOAD,Opcodes.IALOAD]
        ))

        //viewpage
        fragmentMethods.put('androidx/fragment/app/Fragment$setUserVisibleHint(Z)V',new MethodCode(
                'setUserVisibleHint',
                '(Z)V',
                'setUserVisibleHint',
                '(Landroidx/fragment/app/Fragment$;Z)V',
                '',//parent省略
                0,
                2,
                [Opcodes.ALOAD,Opcodes.IALOAD]
        ))

        //FragmentTransaction方式
        fragmentMethods.put('androidx/fragment/app/Fragment$onHiddenChanged(Z)V',new MethodCode(
                'onHiddenChanged',
                '(Z)V',
                'onHiddenChanged',
                '(Landroidx/fragment/app/Fragment$;Z)V',
                '',//parent省略
                0,
                2,
                [Opcodes.ALOAD,Opcodes.IALOAD]
        ))
    }

    static {
        activityMethods.put('android/app/Activity$onCreate(Landroid/os/Bundle;)V',new MethodCode(
                'onCreate',
                '(Landroid/os/Bundle;)V',
                'activityOnCreate',
                '(Landroid/app/Activity;)V',
                '',
                1,
                1,
                [Opcodes.ALOAD]
        ))
        activityMethods.put('android/app/Activity$onResume()V',new MethodCode(
                'onResume',
                '()V',
                'activityOnResume',
                '(Landroid/app/Activity;)V',
                '',
                1,
                1,
                [Opcodes.ALOAD]
        ))

        activityMethods.put('androidx/appcompat/app/AppCompatActivity$onCreate(Landroid/os/Bundle;)V',new MethodCode(
                'onCreate',
                '(Landroid/os/Bundle;)V',
                'activityOnCreate',
                '(Landroidx/appcompat/app/AppCompatActivity;)V',
                '',
                1,
                1,
                [Opcodes.ALOAD]
        ))
        activityMethods.put('androidx/appcompat/app/AppCompatActivity$onResume()V',new MethodCode(
                'onResume',
                '()V',
                'activityOnResume',
                '(Landroidx/appcompat/app/AppCompatActivity;)V',
                '',
                1,
                1,
                [Opcodes.ALOAD]
        ))

    }

}