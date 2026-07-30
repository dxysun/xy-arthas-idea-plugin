import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.18.1"
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// 编译目标：IntelliJ 2026.2(262) 运行于 JBR 25，插件字节码目标设为 21
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    intellijPlatform {
        // 直接依赖本机安装的 IntelliJ IDEA（无需下载 IDE）
        local(providers.gradleProperty("localIdePath"))
        // 原插件在 plugin.xml 中 depends com.intellij.modules.java
        bundledPlugin("com.intellij.java")

        pluginVerifier()
        testFramework(TestFrameworkType.Platform)
    }

    // ==== 原 .iml 中手动管理的第三方库（会被打进插件 lib/ 目录）====
    implementation("org.java-websocket:Java-WebSocket:1.5.3")
    implementation("com.aliyun.oss:aliyun-sdk-oss:3.9.1")
    implementation("commons-io:commons-io:2.7")
    implementation("org.apache.commons:commons-text:1.11.0")
    implementation("com.google.guava:guava:23.0")
    implementation("commons-collections:commons-collections:3.2.2")
    implementation("redis.clients:jedis:3.4.1")
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.321")
    implementation("org.reflections:reflections:0.10.2")
    implementation("com.alibaba.fastjson2:fastjson2:2.0.49")
    implementation("com.alibaba:fastjson:1.2.83")

    // lombok（编译期注解处理，1.18.46 支持 JDK 25 编译器）
    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")
}

intellijPlatform {
    pluginConfiguration {
        version = providers.gradleProperty("pluginVersion")
        ideaVersion {
            sinceBuild = "232"
            untilBuild = provider { null }
        }
    }
    // .form GUI Designer 文件需要字节码织入
    instrumentCode = true
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    // 原插件资源里 template/*.sh 等已在 src/main/resources，无需额外处理
    wrapper {
        gradleVersion = "9.3.0"
    }
}
