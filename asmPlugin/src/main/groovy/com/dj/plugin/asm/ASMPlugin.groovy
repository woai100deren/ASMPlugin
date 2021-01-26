package com.dj.plugin.asm

import com.android.build.gradle.AppExtension
import org.gradle.api.Project
import org.gradle.api.Plugin

public class ASMPlugin implements Plugin<Project> {
    public void apply(Project project) {
        System.out.println("=========ASM Plugin start=========")
        def android = project.extensions.findByType(AppExtension.class)
//        def android =  project.extensions.getByName("asmPlugin")
        LifeCycleTransform transform = new LifeCycleTransform()
        android.registerTransform(transform)
        System.out.println("=========ASM Plugin end=========")
    }
}