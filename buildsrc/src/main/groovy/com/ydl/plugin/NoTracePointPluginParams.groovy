package com.ydl.plugin

/**
 * @author harvie
 */
class NoTracePointPluginParams {
    //插件名称，随意
    String pluginName = ''
    /**
     * 需要修改的目标包名，默认主app module
     */
    public HashSet<String> targetPackages
    /**
     * 是否输出扫描文件
     */
    public boolean outputModifyFile = false

    public boolean isPrintLog = true

    /**
     * 接收拦截事件的类名(含包名的全路径)
     */
    public String targetClassName
}