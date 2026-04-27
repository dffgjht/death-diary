import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"
}

group = "com.memoamber"
version = "1.4.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin { jvmToolchain(17) }

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.runtime)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.xerial:sqlite-jdbc:3.44.1.0")
    implementation("org.bouncycastle:bcprov-jdk18on:1.77")
}

compose.desktop {
    application {
        mainClass = "com.memoamber.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Exe)
            packageName = "MemoAmber"
            packageVersion = "1.4.0"
            vendor = "MemoAmber"
            windows {
                menuGroup = "记忆琥珀"
                upgradeUuid = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
                dirChooser = true
                perUserInstall = true
                shortcut = true
            }
            jvmArgs += listOf("-Xmx512m", "-Dfile.encoding=UTF-8")
        }
    }
}
