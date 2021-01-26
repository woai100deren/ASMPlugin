package com.dj.plugin.asm

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.dj.plugin.asm.lib.LifecycleClassVisitor
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.internal.artifacts.transform.TransformException
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

public class LifeCycleTransform extends Transform {

    /**
     * 设置自定义Transform对应的Task名称
     */
    @Override
    String getName() {
        return "LifeCycleTransform"
    }

    /**
     * 设置自定义Transform接收的文件类型；可选如下：
     * CLASSES(1),   -- 代表只检索.class文件
     * RESOURCES(2); -- 代表检索java标准资源文件
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 代表自定义Transform检索的范围：
     * PROJECT(1),   -- 只有项目内容
     * SUB_PROJECTS(4), -- 只有子项目内容
     * EXTERNAL_LIBRARIES(16), --只有外部类
     * TESTED_CODE(32), -- 由当前变量（包括依赖项）测试的代码
     * PROVIDED_ONLY(64), -- 只提供本地或远程依赖项
     * PROJECT_LOCAL_DEPS(2) --只有项目的本地依赖项（本地jar）
     * SUB_PROJECTS_LOCAL_DEPS(8); --只有子项目的本地依赖项（本地jar）
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.PROJECT_ONLY
    }

    /**
     * 当前Transform是否支持增量编译
     */
    @Override
    boolean isIncremental() {
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
        System.out.println("=========transform=========")

        Collection<TransformInput> transformInputs = transformInvocation.inputs
        TransformOutputProvider outputProvider = transformInvocation.outputProvider
//        if (outputProvider != null) {
//            outputProvider.deleteAll()
//        }

        transformInputs.each { TransformInput transformInput ->
            transformInput.directoryInputs.each { DirectoryInput directoryInput ->
                File dir = directoryInput.file
                if (dir) {
                    dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) { File file ->
                        System.out.println("find class: " + file.name)

                        //对class文件进行读取与解析
                        ClassReader classReader = new ClassReader(file.bytes)
                        //对class文件的写入
                        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                        //访问class文件相应的内容，解析到某一个结构就会通知到ClassVisitor的相应方法
                        ClassVisitor classVisitor = new LifecycleClassVisitor(classWriter)
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


            //对类型为jar文件的input进行遍历
            transformInput.jarInputs.each { JarInput jarInput ->

                //jar文件一般是第三方依赖库jar文件
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //生成输出路径
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }
}