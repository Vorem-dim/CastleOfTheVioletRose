import korlibs.korge.gradle.*

plugins {
    alias(libs.plugins.korge)
}

korge {
    id = "com.sample.demo"
    name = "@string/app_name"
    orientation = Orientation.LANDSCAPE

    icon = File(rootDir, "src/commonMain/resources/images/icon_rose.png")

// To enable all targets at once

    //targetAll()

// To enable targets based on properties/environment variables
    //targetDefault()

// To selectively enable targets

    targetJvm()
    targetJs()
    targetIos()
    targetAndroid()

    serializationJson()
}


dependencies {
    add("commonMainApi", project(":deps"))
    //add("commonMainApi", project(":korge-dragonbones"))
}

