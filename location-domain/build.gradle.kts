plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.github.yuriisurzhykov.purs.domain"
    compileSdk = ProjectProperties.compileSdkVersion

    defaultConfig {
        minSdk = ProjectProperties.minSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = ProjectProperties.javaSourceCompatibility
        targetCompatibility = ProjectProperties.javaTargetCompatibility
    }
    kotlinOptions {
        jvmTarget = ProjectProperties.kotlinJvmTarget
    }
}

dependencies {

    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.locationData.repository)
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}