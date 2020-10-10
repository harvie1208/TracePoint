package com.harvie.plugin.traceplugin

import org.objectweb.asm.Opcodes

class MethodCode{
    String name //源码方法名
    String desc //源码描述
    String agentName //采集数据的方法名
    String agentDesc //采集数据的方法描述
    String parent //方法所在的接口或类
    int paramsStart //参数起始下标
    int paramsCount //参数个数
    List<Opcodes> opcodes //参数类型对应的asm指令

    MethodCode(String name, String desc, String agentName, String agentDesc, String parent, int paramsStart, int paramsCount, List<Opcodes> opcodes) {
        this.name = name
        this.desc = desc
        this.agentName = agentName
        this.agentDesc = agentDesc
        this.parent = parent
        this.paramsStart = paramsStart
        this.paramsCount = paramsCount
        this.opcodes = opcodes
    }
}