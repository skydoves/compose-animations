@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
  repositories {
    mavenLocal()
    google() {
      content {
        includeGroupByRegex("androidx\\..*")
        includeGroupByRegex("com\\.android(\\..*|)")
        includeGroupByRegex("com\\.google\\..*")
      }
      mavenContent { releasesOnly() }
    }
    mavenCentral() {
      mavenContent { releasesOnly() }
    }
    gradlePluginPortal()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
      mavenContent { snapshotsOnly() }
    }
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenLocal()
    google() {
      content {
        includeGroupByRegex("androidx\\..*")
        includeGroupByRegex("com\\.android(\\..*|)")
        includeGroupByRegex("com\\.google\\..*")
      }
      mavenContent { releasesOnly() }
    }
    mavenCentral() {
      mavenContent { releasesOnly() }
    }
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
      mavenContent { snapshotsOnly() }
    }
  }
}

rootProject.name = "hot-reload-animations"
include(":app")
