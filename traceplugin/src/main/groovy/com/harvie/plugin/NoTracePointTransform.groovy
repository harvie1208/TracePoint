package com.harvie.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import groovy.io.FileType
import org.gradle.api.Project

/**
 * @author harvie
 */
class NoTracePointTransform extends Transform{

    private static Project project
    private static BaseExtension android
    //扫描目标包名集合
    private static Set<String> targetPackages = new HashSet<>()

    NoTracePointTransform(Project project) {
        this.project = project
        this.android = project.extensions.getByType(BaseExtension)
        ClassModifyUtil.project = project
        ClassModifyUtil.noTracePointPluginParams = project.noTracePoint

    }

    @Override
    String getName() {
        return "noTracePointTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        //输入类型
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        //作用域 全局
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        //是否增量构建
        return true
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        //核心操作
        long t1 = System.currentTimeMillis()
        HLog.i("transform start: "+t1)

        String clasName = ClassModifyUtil.noTracePointPluginParams.targetClassName
        if (clasName){
            InterceptEventConfig.owner = clasName.replace('.',"/")
        }

        HashSet<String> tempPackages = project.noTracePoint.targetPackages

        if (null != tempPackages){
            for (String tagetPackage : tempPackages){
                String str = tagetPackage.replace(".", File.separator)
                targetPackages.add(str)
            }
        }else {
            String pack = ClassModifyUtil.getPackageName(android)
            String str = pack.replace(".", File.separator)
            targetPackages.add(str)
        }
        HLog.i("targetPackages:"+targetPackages)

        inputs.each {TransformInput input->
            input.jarInputs.each { JarInput jarInput->

                /** 获得输出文件*/
                File dest = outputProvider.getContentLocation(jarInput.file.absolutePath, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                File modifiedJar = null
                modifiedJar = ClassModifyUtil.modifyJarFile(jarInput.file,context.getTemporaryDir(),android,targetPackages)
                if (modifiedJar == null){
                    modifiedJar = jarInput.file
                }
                FileUtils.copyFile(modifiedJar,dest)
            }

            input.directoryInputs.each { DirectoryInput directoryInput->

                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                File dirFile = directoryInput.file

                if (dirFile){
                    HashMap modifyMap = new HashMap()
                    dirFile.traverse(type: FileType.FILES,nameFilter:~/.*\.class/){
                        File classFile ->

//                            HLog.i("directoryInput.file:"+classFile.absolutePath)

                            if (ClassModifyUtil.fileIsTagetPackage(classFile.absolutePath,targetPackages)){
                                File modified = modifyClassFile(dirFile,classFile,context.getTemporaryDir())
                                if (modified!=null){
                                    modifyMap.put(classFile.absolutePath.replace(dirFile.absolutePath,''),modified)
                                }
                            }
                    }
                    FileUtils.copyDirectory(directoryInput.file,dest)
                    modifyMap.entrySet().each {
                        Map.Entry<String,File> en->
                            File target = new File(dest.absolutePath+en.getKey())
//                            HLog.i("en.getValue():"+en.getValue().absolutePath+"  target:"+target.absolutePath)
                            FileUtils.copyFile(en.getValue(),target)
                            if (ClassModifyUtil.noTracePointPluginParams.outputModifyFile){
                                ClassModifyUtil.savemodifedJar(en.getValue())
                            }
                            en.getValue().delete()
                    }
                }

            }
        }
        long t2 = System.currentTimeMillis()
        HLog.i("transform end 耗时: "+(t2-t1)+"毫秒")
    }

    static File modifyClassFile(File dir,File classFile,File tempDir){
        File modified
        if (ClassModifyUtil.shouldModifyClass(classFile.absolutePath,android)){
            String className = classFile.absolutePath.replace(dir.absolutePath+File.separator,'').replace(File.separator,".")
            byte[] source = ClassModifyUtil.modifyClass(classFile)
            modified = new File(tempDir,className.replace(".",""))
            if (modified.exists()){
                modified.delete()
            }
            modified.createNewFile()
            FileOutputStream fos =  new FileOutputStream(modified)
            fos.write(source)
            fos.flush()
            fos.close()
        }
        return modified
    }

}