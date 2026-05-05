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
package com.skydoves.hotreloadanimations.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.skydoves.hotreloadanimations.animations.AnimationCatalog

@Composable
fun AnimationsApp() {
  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = "home") {
    composable("home") {
      HomeScreen(
        examples = AnimationCatalog.entries,
        onClick = { id -> navController.navigate("example/$id") },
      )
    }
    composable("example/{id}") { backStack ->
      val id = backStack.arguments?.getString("id")?.toIntOrNull() ?: 1
      val entry = AnimationCatalog.find(id)
      ExampleScreen(
        entry = entry,
        onBack = {
          if (navController.previousBackStackEntry != null) {
            navController.popBackStack()
          }
        },
      )
    }
  }
}
