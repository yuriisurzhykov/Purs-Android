plugins {
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.github.yuriisurzhykov.purs.data.cache"
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

    implementation(projects.core)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.javax.inject)

    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}