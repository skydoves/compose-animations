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
package com.skydoves.hotreloadanimations.animations

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AnimationExample3() {
  val COLOR_TRANSITION_MS = 600 // Tweak how slow/fast colors morph
  // Edit palette colors live to swap the theme.
  val PALETTE = listOf(
    Color(0xFF63CCD9) to "Coral",
    Color(0xFFC6FF00) to "Lime",
    Color(0xFF40C4FF) to "Sky",
    Color(0xFF47CD72) to "Lavender",
  )
  val HERO_HEIGHT_DP = 180 // Make the hero box taller/shorter

  var selectedIndex by remember { mutableStateOf(0) }
  val (selected, selectedName) = PALETTE[selectedIndex.coerceIn(0, PALETTE.lastIndex)]

  val animatedBg by animateColorAsState(
    targetValue = selected,
    animationSpec = tween(durationMillis = COLOR_TRANSITION_MS),
    label = "bg",
  )
  val animatedContent by animateColorAsState(
    targetValue = if (selected.luminance() > 0.5f) Color(0xFF202020) else Color.White,
    animationSpec = tween(durationMillis = COLOR_TRANSITION_MS),
    label = "content",
  )

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "animateColorAsState swatches",
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onSurface,
    )

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(HERO_HEIGHT_DP.dp)
        .clip(RoundedCornerShape(24.dp))
        .background(animatedBg),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = selectedName,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = animatedContent,
      )
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      PALETTE.forEachIndexed { index, (color, name) ->
        Column(
          modifier = Modifier
            .weight(1f)
            .clickable { selectedIndex = index },
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(color),
          )
          Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
      }
    }

    Text(
      text = "Tap a swatch to morph the hero color.",
      modifier = Modifier.padding(top = 4.dp),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}
