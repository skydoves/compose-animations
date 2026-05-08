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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

@Composable
fun AnimationExample11() {
  val MORPH_DURATION_MS = 500 // morph speed
  val PLAY_COLOR = Color(0xFFDE2263) // hex color when in PLAY state
  val PAUSE_COLOR = Color(0xFF42A5F5) // hex color when in PAUSE state
  val ICON_BOX_DP = 200 // size of the morph stage

  var isPlaying by remember { mutableStateOf(false) }
  val morphProgress by animateFloatAsState(
    targetValue = if (isPlaying) 1f else 0f,
    animationSpec = tween(durationMillis = MORPH_DURATION_MS),
    label = "morph",
  )
  val bgColor by animateColorAsState(
    targetValue = if (isPlaying) PAUSE_COLOR else PLAY_COLOR,
    animationSpec = tween(durationMillis = MORPH_DURATION_MS),
    label = "bg",
  )

  Column(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text("Play / Pause Morph", style = MaterialTheme.typography.titleMedium)
    Text(
      "Tap the circle to morph between play and pause.",
      style = MaterialTheme.typography.bodySmall,
    )
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
    ) {
      Box(
        modifier = Modifier
          .size(ICON_BOX_DP.dp)
          .background(color = bgColor, shape = CircleShape)
          .clickable { isPlaying = !isPlaying },
        contentAlignment = Alignment.Center,
      ) {
        PlayPauseMorph(progress = morphProgress, iconBoxDp = ICON_BOX_DP)
      }
    }
    Text(
      text = if (isPlaying) "State: PLAYING" else "State: PAUSED",
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}

@Composable
private fun PlayPauseMorph(progress: Float, iconBoxDp: Int) {
  Canvas(modifier = Modifier.size((iconBoxDp / 2).dp)) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f
    val triHalf = w * 0.32f
    val barHalfW = w * 0.12f
    val barGap = w * 0.18f

    // Triangle (PLAY) corners.
    val triTopX = cx - triHalf * 0.65f
    val triTopY = cy - triHalf
    val triBotX = cx - triHalf * 0.65f
    val triBotY = cy + triHalf
    val triRightX = cx + triHalf
    val triRightY = cy

    // Pause bars target corners (left bar + right bar collapsed into one quad each).
    val leftBarL = cx - barGap - barHalfW
    val leftBarR = cx - barGap + barHalfW
    val rightBarL = cx + barGap - barHalfW
    val rightBarR = cx + barGap + barHalfW
    val barTop = cy - triHalf
    val barBot = cy + triHalf

    // Left shape morphs triangle's top+bottom+right into the LEFT pause bar.
    val leftPath = Path().apply {
      moveTo(lerp(triTopX, leftBarL, progress), lerp(triTopY, barTop, progress))
      lineTo(lerp(triRightX, leftBarR, progress), lerp(triRightY, barTop, progress))
      lineTo(lerp(triRightX, leftBarR, progress), lerp(triRightY, barBot, progress))
      lineTo(lerp(triBotX, leftBarL, progress), lerp(triBotY, barBot, progress))
      close()
    }
    // Right shape only appears as we cross 50% (it grows from the right edge of triangle).
    val rightPath = Path().apply {
      moveTo(lerp(triRightX, rightBarL, progress), lerp(triRightY, barTop, progress))
      lineTo(lerp(triRightX, rightBarR, progress), lerp(triRightY, barTop, progress))
      lineTo(lerp(triRightX, rightBarR, progress), lerp(triRightY, barBot, progress))
      lineTo(lerp(triRightX, rightBarL, progress), lerp(triRightY, barBot, progress))
      close()
    }
    drawPath(path = leftPath, color = Color.White)
    drawPath(path = rightPath, color = Color.White)
    // Anchor a tiny offset so Canvas always remeasures cleanly on reload.
    drawCircle(color = Color.Transparent, radius = 0f, center = Offset(cx, cy))
  }
}

private fun lerp(start: Float, end: Float, fraction: Float): Float = start + (end - start) * fraction
