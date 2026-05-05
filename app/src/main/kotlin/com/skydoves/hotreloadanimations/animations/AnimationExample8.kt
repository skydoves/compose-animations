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

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AnimationExample8() {
  val ROTATION_DURATION_MS = 1400 // spin speed
  val SWEEP_DURATION_MS = 1200
  val SPINNER_COLOR = Color(0xFF1A94D2) // try any color
  val SPINNER_STROKE_DP = 15.dp
  val SPINNER_SIZE_DP = 163.dp
  val SWEEP_MIN = 10f
  val SWEEP_MAX = 290f

  // InfiniteTransition.animateFloat ignores animationSpec changes (Compose only re-checks
  // initialValue/targetValue). Wrap in key() so duration edits trigger a fresh state.
  val transition = rememberInfiniteTransition(label = "spinner")

  val rotation by key(ROTATION_DURATION_MS) {
    transition.animateFloat(
      initialValue = 0f,
      targetValue = 360f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = ROTATION_DURATION_MS, easing = LinearEasing),
        repeatMode = RepeatMode.Restart,
      ),
      label = "rotation",
    )
  }

  val sweep by key(SWEEP_DURATION_MS) {
    transition.animateFloat(
      initialValue = SWEEP_MIN,
      targetValue = SWEEP_MAX,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = SWEEP_DURATION_MS, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse,
      ),
      label = "sweep",
    )
  }

  Column(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "Custom Loading Spinner",
      fontWeight = FontWeight.SemiBold,
    )
    Box(
      modifier = Modifier.fillMaxWidth().heightIn(min = 280.dp),
      contentAlignment = Alignment.Center,
    ) {
      Canvas(modifier = Modifier.size(SPINNER_SIZE_DP)) {
        val strokePx = SPINNER_STROKE_DP.toPx()
        val inset = strokePx / 2f
        val arcSize = Size(size.width - strokePx, size.height - strokePx)
        rotate(degrees = rotation) {
          drawArc(
            color = SPINNER_COLOR,
            startAngle = 0f,
            sweepAngle = sweep,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = Stroke(width = strokePx, cap = StrokeCap.Round),
          )
        }
      }
    }
  }
}
