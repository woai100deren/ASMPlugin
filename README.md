### 如何编写一个gradle插件，以及采用asm框架，对java字节码插装

------

一、新建一个module，java、android都可以。新建完后，只保留src/main文件夹以及build.gradle文件，其他全部删除都可以。

二、build.gradle修改为：

```groovy
plugins {
    id 'java-gradle-plugin'
    id 'groovy'

    // 应用 Maven Publish Plugin 发布插件
    id 'maven-publish'
}

repositories {
    jcenter()
}

dependencies {
    //引入gradle，必须引入，因为transform需要用到，如果不开发transform，则可不引入
    implementation "com.android.tools.build:gradle:4.1.1"

    //gradle sdk
    implementation gradleApi()
    //groovy sdk
    implementation localGroovy()

}

gradlePlugin {
    // Define the plugin
    plugins {
        asmPlugin { //transform task的名字，随便起的
            id = 'com.dj.plugin.asm'//插件的id，与应用插件时写的 apply plugin: 'com.dj.plugin.asm' 名字要一致
            implementationClass = 'com.dj.plugin.asm.ASMPlugin'//插件具体实现类的类名
        }
    }
}

publishing {
    publications {
        asmPlugin(MavenPublication) {
            // 插件的组ID，建议设置为插件的包名
            groupId = 'com.dj.plugin'
            // 翻译过来是 工件ID，我的理解是插件的名字
            artifactId = 'asmPlugin'
            version = '0.0.1'
            // 组件类型，我们的插件其实就是Java组件
            from components.java
        }
    }

    repositories {
        maven {
            // $rootProject 表示你项目的根目录
            url = "$rootDir/repo"
        }
    }
}
```

三、在main文件夹下新建groovy文件夹。然后在groovy文件夹下，新建com、dj、plugin、asm文件夹，在asm文件夹下新建ASMPlugin.groovy，此处的路径实际上与上面的`implementationClass`必须要一致。

四、编写groovy，这里不阐述，参考demo中的代码。

五、插件开发完后，发布到maven，这里不阐述。

六、发布完后，在工程下的buuild.gradle中引入插件

```groovy
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
        //此处引入maven
        maven {
            url uri("$rootDir/repo")
        }
    }
    dependencies {
        classpath "com.android.tools.build:gradle:4.1.1"
		//此处引入插件
        classpath "com.dj.plugin:asmPlugin:0.0.1"
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
```

在app主module中使用插件

```groovy
plugins {
    id 'com.android.application'
	//使用插件，这里的名称必须与上面asmPlugin下定义的id的名称一致
    id 'com.dj.plugin.asm'
}
......
```

七、使用`@DotAnnotation`注解，实现埋点统计

```java
findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
    @DotAnnotation("textView click")
    @Override
    public void onClick(View v) {
        Log.e("123","456");
    }
});
```

具体的代码细节请查看源码。