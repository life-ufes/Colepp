// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.dagger.hilt) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.compose.compiler) apply false
}

 buildscript {
     val kotlin_version by extra("1.9.21")

     repositories {
         google()
         mavenCentral()
     }

     dependencies {
         classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
     }
 }