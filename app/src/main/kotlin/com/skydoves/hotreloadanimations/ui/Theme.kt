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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = Color(0xFF6750A4),
  onPrimary = Color.White,
  primaryContainer = Color(0xFFEADDFF),
  onPrimaryContainer = Color(0xFF21005D),
  secondary = Color(0xFF625B71),
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFE8DEF8),
  onSecondaryContainer = Color(0xFF1D192B),
  tertiary = Color(0xFF7D5260),
  background = Color(0xFFFAFAFA),
  surface = Color.White,
  surfaceVariant = Color(0xFFE7E0EC),
  onSurfaceVariant = Color(0xFF49454F),
)

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFFD0BCFF),
  onPrimary = Color(0xFF381E72),
  primaryContainer = Color(0xFF4F378B),
  onPrimaryContainer = Color(0xFFEADDFF),
  secondary = Color(0xFFCCC2DC),
  onSecondary = Color(0xFF332D41),
  secondaryContainer = Color(0xFF4A4458),
  onSecondaryContainer = Color(0xFFE8DEF8),
  tertiary = Color(0xFFEFB8C8),
  background = Color(0xFF121212),
  surface = Color(0xFF1C1B1F),
  surfaceVariant = Color(0xFF49454F),
  onSurfaceVariant = Color(0xFFCAC4D0),
)

@Composable
fun HotReloadAnimationsTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    content = content,
  )
}
