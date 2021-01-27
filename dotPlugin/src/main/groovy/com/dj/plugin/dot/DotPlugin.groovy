package com.dj.plugin.dot

import com.android.build.gradle.AppExtension
import org.gradle.api.Project
import org.gradle.api.Plugin

public class DotPlugin implements Plugin<Project> {
    public void apply(Project project) {
        def android = project.extensions.findByType(AppExtension.class)
        DotTransform transform = new DotTransform(project)
        android.registerTransform(transform)
    }
}