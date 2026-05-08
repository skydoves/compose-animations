/*
 * Designed and developed by 2026 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.hotswan.compiler)
}

android {
  namespace = "com.skydoves.hotreloadanimations"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.skydoves.hotreloadanimations"
    minSdk = 23
    targetSdk = 36
    versionCode = 1
    versionName = "1.0.0"
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  buildFeatures {
    compose = true
  }

  lint {
    abortOnError = true
  }
}

kotlin {
  jvmToolchain(17)
  compilerOptions {
    freeCompilerArgs.addAll(
      "-Xno-param-assertions",
      "-Xno-call-assertions",
      "-Xno-receiver-assertions",
      "-Xopt-in=androidx.compose.material3.ExperimentalMaterial3Api",
      "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi",
      "-Xopt-in=androidx.compose.animation.ExperimentalSharedTransitionApi",
      "-Xopt-in=androidx.compose.foundation.ExperimentalFoundationApi",
      "-Xopt-in=androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi",
    )
  }
}

dependencies {
  debugImplementation(libs.hotswan.preview)

  implementation(libs.androidx.core)
  implementation(libs.androidx.activity.compose)

  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.foundation.layout)
  implementation(libs.androidx.compose.animation)
  implementation(libs.androidx.compose.animation.graphics)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui.tooling.preview)
  debugImplementation(libs.androidx.compose.ui.tooling)

  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.lifecycle.viewModelCompose)
  implementation(libs.androidx.core.splashscreen)

  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.coroutines.android)
}
