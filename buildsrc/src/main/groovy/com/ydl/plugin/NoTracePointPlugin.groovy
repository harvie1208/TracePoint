package com.ydl.plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author harvie
 */
class NoTracePointPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        project.extensions.create(ClassModifyUtil.CONFIG_NAME,NoTracePointPluginParams)
        registerTransform(project)
    }

    def static registerTransform(Project project){
        BaseExtension extension = project.extensions.getByType(BaseExtension)
        NoTracePointTransform transform = new NoTracePointTransform(project)
        extension.registerTransform(transform)
    }
}