enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Purs-Android"
include(":app")
include(":core")
include(":core:android")
include(":location-data:cloud")
include(":location-data:cache")
include(":location-data:repository")
include(":location-domain")
include(":ui-features:location-details")
include(":core:data")
include(":location-uikit")
