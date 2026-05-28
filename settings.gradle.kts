pluginManagement {
    repositories {
        maven { url = uri("https://mirrors.tuna.tsinghua.edu.cn/maven2/") }
        maven { url = uri("https://mirrors.tuna.tsinghua.edu.cn/gradle/plugins/") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://mirrors.tuna.tsinghua.edu.cn/maven2/") }
        google()
        mavenCentral()
    }
}

rootProject.name = "CosPose"
include(":app")
