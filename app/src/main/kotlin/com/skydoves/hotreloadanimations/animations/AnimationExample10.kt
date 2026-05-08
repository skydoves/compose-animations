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

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

private data class EasingEntry(val name: String, val easing: Easing, val color: Color)

@Composable
fun AnimationExample10() {
  val RACE_DURATION_MS = 2500 // longer = easier to compare easings
  val TRACK_HEIGHT_DP = 42 // thicker tracks
  val RUNNER_SIZE_DP = 34 // runner circle size

  var progress by remember { mutableFloatStateOf(0f) }
  val target by animateFloatAsState(
    targetValue = progress,
    animationSpec = tween(durationMillis = RACE_DURATION_MS, easing = LinearEasing),
    label = "noop",
  )

  // ↑ keep one driver alive even when individual rows recompose
  @Suppress("UNUSED_VARIABLE")
  val tick = target

  val entries = listOf(
    EasingEntry("LinearEasing", LinearEasing, Color(0xFFEF5350)),
    EasingEntry("FastOutSlowInEasing", FastOutSlowInEasing, Color(0xFFAB47BC)),
    EasingEntry("FastOutLinearInEasing", FastOutLinearInEasing, Color(0xFF42A5F5)),
    EasingEntry("LinearOutSlowInEasing", LinearOutSlowInEasing, Color(0xFF26A69A)),
    EasingEntry("EaseInOutCubic", EaseInOutCubic, Color(0xFFFFA726)),
    EasingEntry("EaseOutBounce", EaseOutBounce, Color(0xFF8D6E63)),
  )

  Column(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text("Easing Showcase", style = MaterialTheme.typography.titleMedium)
    Text(
      "Tap Race! to compare easings side by side.",
      style = MaterialTheme.typography.bodySmall,
    )
    entries.forEach { entry ->
      RaceTrack(
        entry = entry,
        progress = progress,
        durationMs = RACE_DURATION_MS,
        trackHeightDp = TRACK_HEIGHT_DP,
        runnerSizeDp = RUNNER_SIZE_DP,
      )
    }
    Button(onClick = { progress = if (progress == 0f) 1f else 0f }) {
      Text(if (progress == 0f) "Race!" else "Reset")
    }
  }
}

@Composable
private fun RaceTrack(
  entry: EasingEntry,
  progress: Float,
  durationMs: Int,
  trackHeightDp: Int,
  runnerSizeDp: Int,
) {
  val animated by animateFloatAsState(
    targetValue = progress,
    animationSpec = tween(durationMillis = durationMs, easing = entry.easing),
    label = entry.name,
  )
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text(entry.name, style = MaterialTheme.typography.labelMedium)
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxWidth()
        .height(trackHeightDp.dp)
        .background(
          color = MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(trackHeightDp.dp / 2),
        ),
    ) {
      val density = LocalDensity.current
      val trackWidthPx = with(density) { (maxWidth - runnerSizeDp.dp).toPx() }
      Box(
        modifier = Modifier
          .align(Alignment.CenterStart)
          .padding(horizontal = 4.dp)
          .offset { IntOffset((trackWidthPx * animated).toInt(), 0) }
          .size(runnerSizeDp.dp)
          .background(color = entry.color, shape = CircleShape),
      )
    }
  }
}
