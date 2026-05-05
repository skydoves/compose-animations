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

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimationExample1() {
  val EXPAND_DURATION_MS = 400 // Tweak duration/easing to feel different unfolds
  val EXPAND_EASING = FastOutSlowInEasing // Try LinearEasing or FastOutLinearInEasing
  val COLLAPSED_HEIGHT: Dp = 160.dp // Tweak freely, the value updates live on save
  val EXPANDED_HEIGHT: Dp = 330.dp // Tweak freely, the value updates live on save

  var isExpanded by remember { mutableStateOf(false) }
  val animatedHeight by animateDpAsState(
    targetValue = if (isExpanded) EXPANDED_HEIGHT else COLLAPSED_HEIGHT,
    animationSpec = tween(
      durationMillis = EXPAND_DURATION_MS,
      easing = EXPAND_EASING,
    ),
    label = "cardHeight",
  )

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "animateDpAsState on an expandable card",
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onSurface,
    )

    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(animatedHeight)
        .clickable {
          isExpanded = !isExpanded
        },
      shape = MaterialTheme.shapes.medium,
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
      ),
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "Tap to ${if (isExpanded) "collapse" else "expand"}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
          )
          Text(
            text = if (isExpanded) "v" else ">",
            style = MaterialTheme.typography.titleMedium,
          )
        }

        Text(
          text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
          style = MaterialTheme.typography.bodyMedium,
        )
        Text(
          text = "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
          style = MaterialTheme.typography.bodyMedium,
        )
        Text(
          text = "Ut enim ad minim veniam, quis nostrud exercitation ullamco.",
          style = MaterialTheme.typography.bodyMedium,
        )
        Text(
          text = "Duis aute irure dolor in reprehenderit in voluptate velit esse.",
          style = MaterialTheme.typography.bodyMedium,
        )
        Text(
          text = "Excepteur sint occaecat cupidatat non proident, sunt in culpa.",
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }
  }
}
