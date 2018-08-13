pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "guru.stefma.bintrayrelease") {
                useModule("guru.stefma.bintrayrelease:bintrayrelease:${requested.version}")
            }
            if(requested.id.id.startsWith("com.android.")) {
                useModule("com.android.tools.build:gradle:$requested.id.version")
            }
        }
    }
}

rootProject.name = "bintray-release"