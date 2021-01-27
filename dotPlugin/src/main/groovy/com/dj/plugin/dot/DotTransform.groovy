package com.dj.plugin.dot

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.dj.plugin.dot.lib.DotClassVisitor
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

public class DotTransform extends Transform {
    private static Project project;
    public DotTransform(Project project){
        this.project = project;
    }

    @Override
    String getName() {
        //transform任务名称，随意
        return "dotTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        //输入类型 class文件
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        //作用域 全局工程
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        //是否增量构建
        return false
    }

    /**
     * 获取所有的.class文件，
     * inputs: 传过来的输入流
     * outputProvider： 输出的目录，必须将修改后的文件复制到输出目录
     * Collection<TransformInput> inputs = transformInvocation.inputs
     * TransformOutputProvider transformOutputProvider = transformInvocation.outputProvider
     */
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        Collection<TransformInput> transformInputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
        transformInputs.each { TransformInput transformInput ->
            transformInput.directoryInputs.each { DirectoryInput directoryInput ->
                File dir = directoryInput.file
                if (dir) {
                    dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) { File file ->
                        //对class文件进行读取与解析
                        ClassReader classReader = new ClassReader(file.bytes)
                        //对class文件的写入
                        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                        //访问class文件相应的内容，解析到某一个结构就会通知到ClassVisitor的相应方法
                        ClassVisitor classVisitor = new DotClassVisitor(classWriter)
                        //依次调用 ClassVisitor接口的各个方法
                        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
                        //toByteArray方法会将最终修改的字节码以 byte 数组形式返回。
                        byte[] bytes = classWriter.toByteArray()

                        //通过文件流写入方式覆盖掉原先的内容，实现class文件的改写。
                        FileOutputStream outputStream = new FileOutputStream(file.path)
                        outputStream.write(bytes)
                        outputStream.close()
                    }
                }
                //处理完输入文件后把输出传给下一个文件
                def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes,
                        directoryInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            transformInput.jarInputs.each { JarInput jarInput->
                //jar文件一般是第三方依赖库jar文件
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //生成输出路径
                def destJar = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, destJar)
            }
        }
    }
}