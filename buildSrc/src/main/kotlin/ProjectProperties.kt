import org.gradle.api.JavaVersion

object ProjectProperties {

    val javaSourceCompatibility = JavaVersion.VERSION_19
    val javaTargetCompatibility = JavaVersion.VERSION_19

    const val kotlinJvmTarget = "19"
    const val minSdkVersion = 24
    const val targetSdkVersion = 34
    const val compileSdkVersion = 34
}