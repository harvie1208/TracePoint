package com.ydl.plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * @author harvie
 * class修改工具类
 */
class ClassModifyUtil{

    public static final String CONFIG_NAME = 'noTracePoint'
    public static Project project
    public static NoTracePointPluginParams noTracePointPluginParams

    static byte[] modifyClass(File classFile){
        byte[] classByte = FileUtils.file2byte(classFile)
        ClassWriter cw =  new ClassWriter(ClassWriter.COMPUTE_MAXS)
        HClassVisitor hc = new HClassVisitor(cw)
        ClassReader cr = new ClassReader(classByte)
        cr.accept(hc,ClassReader.SKIP_DEBUG)
        byte[] sultBytes =  cw.toByteArray()
        return sultBytes
    }

    static byte[] modifyClass(InputStream inputStream){
        byte[] classByte = FileUtils.file2byte(inputStream)
        ClassWriter cw =  new ClassWriter(ClassWriter.COMPUTE_MAXS)
        HClassVisitor hc = new HClassVisitor(cw)
        ClassReader cr = new ClassReader(classByte)
        cr.accept(hc,ClassReader.SKIP_DEBUG)
        byte[] sultBytes =  cw.toByteArray()
        return sultBytes
    }


    /**
     * 遍历jar包class
     * @param jarFile
     * @param tempDir
     * @param android
     */
    static File modifyJarFile(File jarFile, File tempDir, BaseExtension android,Set<String> targetPackages){
        if (jarFile){
            //设置输出目录
            def optJar = new File(tempDir,jarFile.name)
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))

            def file  = new JarFile(jarFile)
            Enumeration enumeration = file.entries()
            while (enumeration.hasMoreElements()){
                JarEntry jarEntry = enumeration.nextElement()

                String entryName = jarEntry.getName()

                ZipEntry zipEntry = new ZipEntry(entryName)
                jarOutputStream.putNextEntry(zipEntry)

                byte[] modifiedClassBytes = null

                if (entryName.endsWith(".class")){
                    if (fileIsTagetPackage(entryName,targetPackages)) {
                        if (shouldModifyClass(entryName, android)) {
                            modifiedClassBytes = modifyClass(file.getInputStream(jarEntry))
                            if (modifiedClassBytes) {
                                if (noTracePointPluginParams.outputModifyFile){
                                    savemodifedJar(entryName,modifiedClassBytes)
                                }
                            }
                        }
                    }
                }

                if (modifiedClassBytes == null){

                    modifiedClassBytes = FileUtils.file2byte(file.getInputStream(jarEntry))
                }
                jarOutputStream.write(modifiedClassBytes)
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            file.close()
            return optJar
        }
        return null
    }

    static String getPackageName(BaseExtension android){
        String packageName = ""
        try {
            def manifestFile = android.sourceSets.main.manifest.srcFile
            packageName = new XmlParser().parse(manifestFile).attribute("package")
        }catch(Exception e){
            e.printStackTrace()
        }
        return packageName
    }

    /**
     * 判断当前类是否需要修改
     * @param classFile
     * @return
     */
    static boolean shouldModifyClass(String className,BaseExtension android){
        String entryName = className.replace(File.separator,".")
        if (!entryName.contains("R\$") && !entryName.contains("R.class") && !entryName.contains("BuildConfig.class")){
            return true
        }
        return false
    }

    /**
     * 保存已修改的类，方便查看代码是否成功插入
     * @param file
     * @return
     */
    static savemodifedJar(File file){
        File pluginTmpDir = new File(project.buildDir, 'trace-point')
        if (!pluginTmpDir.exists()) {
            pluginTmpDir.mkdir()
        }
        File ajrFIle = new File(pluginTmpDir,file.getName()+".class")
        if (ajrFIle.exists()){
            ajrFIle.delete()
        }

        FileUtils.copyFile(file,ajrFIle)
    }

    /**
     * 保存已修改的类，方便查看
     * @param file
     * @return
     */
    static savemodifedJar(String fileName,byte[] bytes){
        File pluginTmpDir = new File(project.buildDir, 'trace-point')
        if (!pluginTmpDir.exists()) {
            pluginTmpDir.mkdir()
        }
        FileUtils.byte2File(bytes,pluginTmpDir.absolutePath,fileName)
    }

    /**
     * 判断当前file是否在目标包中
     * @return
     */
    static boolean fileIsTagetPackage(String filePath,Set<String> targetPackages){
        String filePath2 = filePath.replace('/',File.separator)

        Iterator<String> it = targetPackages.iterator()
//        HLog.i("fileIsTagetPackage:"+filePath)
        while (it.hasNext()){
            String pack = it.next()
            if(filePath2.contains(pack)){
                return true
            }
        }
        return false
    }
}