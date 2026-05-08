plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.hotswan.compiler) apply false
  alias(libs.plugins.spotless)
}

subprojects {
  apply(plugin = rootProject.libs.plugins.spotless.get().pluginId)

  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("**/build/**/*.kt")
      ktlint().editorConfigOverride(
        mapOf(
          "indent_size" to 2,
          "continuation_indent_size" to 2,
          "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
          "ktlint_standard_property-naming" to "disabled",
          "ktlint_standard_max-line-length" to "disabled",
        ),
      )
      trimTrailingWhitespace()
      endWithNewline()
    }
    format("kotlinLicense") {
      target("**/*.kt")
      targetExclude(
        "**/build/**/*.kt",
        "**/AnimationExample22.kt",
      )
      licenseHeaderFile(
        rootProject.file("spotless/copyright.kt"),
        "(^(?![\\/ ]\\*).*$)",
      )
    }
    format("kts") {
      target("**/*.kts")
      targetExclude("**/build/**/*.kts")
      licenseHeaderFile(rootProject.file("spotless/copyright.kts"), "(^(?![\\/ ]\\*).*$)")
      trimTrailingWhitespace()
      endWithNewline()
    }
    format("xml") {
      target("**/*.xml")
      targetExclude("**/build/**/*.xml")
      licenseHeaderFile(rootProject.file("spotless/copyright.xml"), "(<[^!?])")
    }
  }
}
