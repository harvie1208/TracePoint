package com.ydl.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * @author harvie
 * asm 字节码操作工具类
 */
class HClassVisitor extends ClassVisitor{

    private String[] interfaces
    private String superName
    private String className

    private ClassVisitor classVisitor

    //记录已访问的fragment方法
    private HashSet<String> methodName = new HashSet<>();

    HClassVisitor(ClassVisitor cv){
        super(Opcodes.ASM7,cv)
        this.classVisitor = cv
    }

    /**
     * 访问类头部信息
     * @param version
     * @param access
     * @param name
     * @param signature
     * @param superName
     * @param interfaces
     */
    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.interfaces = interfaces
        this.superName = superName
        this.className = name
//        this.className = name.contains('$')?name.substring(0,name.indexOf('$')):name
        super.visit(version, access, name, signature, superName, interfaces)
    }

    /**
     * 访问类方法
     * @param access
     * @param name
     * @param desc
     * @param signature
     * @param exceptions
     * @return
     */
    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod( access,  name,  desc,  signature, exceptions)

        String superNameDesc = superName+'$'+name+desc

        return new MethodVisitor(this.api, mv){

            @Override
            void visitCode() {

                //点击事件
                if (interfaces!=null && interfaces.length>0){

                    MethodCode methodCode = InterceptEventConfig.interfaceMethods.get(name+desc)
                    if(methodCode!=null){
                        mv.visitVarInsn(Opcodes.ALOAD, 1)
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, InterceptEventConfig.owner, methodCode.agentName, methodCode.agentDesc, false)
                    }
                }

                //activity生命周期hook
                if (instanceOfActivity(superName)){
                    MethodCode methodCode = InterceptEventConfig.activityMethods.get(superNameDesc)
                    if (methodCode!=null){
                        methodName.add(superNameDesc)
                        mv.visitVarInsn(Opcodes.ALOAD, 0)
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, InterceptEventConfig.owner, methodCode.agentName, methodCode.agentDesc, false)
                    }
                }
                super.visitCode()
            }

            @Override
            void visitInsn(int opcode) {
                //fragment 页面hook
                if (instanceOfFragemnt(superName)) {
                    MethodCode methodCode = InterceptEventConfig.fragmentMethods.get(superNameDesc)
                    if (methodCode != null) {
                        methodName.add(superNameDesc)
                        if (opcode == Opcodes.RETURN) {
                            mv.visitVarInsn(Opcodes.ALOAD, 0)
                            mv.visitVarInsn(Opcodes.ILOAD, 1)
                            mv.visitMethodInsn(Opcodes.INVOKESTATIC, InterceptEventConfig.owner, methodCode.agentName, methodCode.agentDesc, false)
                        }
                    }
                }
                super.visitInsn(opcode)
            }
        }
    }

    @Override
    void visitEnd() {
        if (instanceOfActivity(superName)){
            //防止activity没有复写oncreate方法，再次检测添加
            Iterator iterator = InterceptEventConfig.activityMethods.keySet().iterator()
            while (iterator.hasNext()) {
                String key = iterator.next()
                MethodCode methodCell = InterceptEventConfig.activityMethods.get(key)

                if (methodName.contains(key) || !key.contains(superName)) {
                    continue
                }
                //添加需要的生命周期方法
                MethodVisitor methodVisitor = classVisitor.visitMethod(Opcodes.ACC_PUBLIC, methodCell.name, methodCell.desc, null, null)
                methodVisitor.visitCode()
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)

                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
                if (key.contains('onCreate(Landroid/os/Bundle;)V')) {
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
                }
                methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, methodCell.name, methodCell.desc, false)
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, InterceptEventConfig.owner, methodCell.agentName, methodCell.agentDesc, false)
                methodVisitor.visitInsn(Opcodes.RETURN)
                methodVisitor.visitMaxs(2, 2)
                methodVisitor.visitEnd()
            }

        }else if (instanceOfFragemnt(superName)){
            Iterator iterator = InterceptEventConfig.fragmentMethods.keySet().iterator()
            while (iterator.hasNext()){
                String key = iterator.next()
                MethodCode methodCell = InterceptEventConfig.fragmentMethods.get(key)
                if (methodName.contains(key) || !key.contains(superName)){
                    continue
                }
                //添加需要的生命周期方法
                MethodVisitor methodVisitor = classVisitor.visitMethod(Opcodes.ACC_PUBLIC, methodCell.name, methodCell.desc, null, null)
                methodVisitor.visitCode()
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
                methodVisitor.visitVarInsn(Opcodes.ILOAD, 1)
                methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, methodCell.name, methodCell.desc, false)
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
                methodVisitor.visitVarInsn(Opcodes.ILOAD, 1)
                methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, InterceptEventConfig.owner, methodCell.agentName, methodCell.agentDesc, false)
                methodVisitor.visitInsn(Opcodes.RETURN)
                methodVisitor.visitMaxs(2, 2)
                methodVisitor.visitEnd()
            }

        }
        super.visitEnd()
    }
/**
     * 判断是否是fragment子类
     * @param superName
     */
    private static boolean instanceOfFragemnt(String superName){
        return superName == "android/app/Fragment" || superName == "androidx/fragment/app/Fragment"
    }

    private static boolean instanceOfActivity(String superName){
        return superName == 'androidx/appcompat/app/AppCompatActivity' || superName == 'android/app/Activity'
    }

}