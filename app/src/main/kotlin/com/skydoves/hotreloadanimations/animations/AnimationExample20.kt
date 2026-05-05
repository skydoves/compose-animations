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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimationExample20() {
  val PENDULUM_COUNT = 18                         // 5 (sparse) ↔ 30 (dense rainbow wave)
  val SYNC_PERIOD_SEC = 30f                       // 10 (frantic) ↔ 60 (slow contemplation)
  val BASE_OSCILLATIONS = 20                      // swings the slowest pendulum makes per sync
  val MAX_ANGLE_DEG = 32f                         // 10 (subtle) ↔ 45 (dramatic swings)
  val PIVOT_TOP_FRACTION = 0.05f                  // vertical position of the pivot row
  val PENDULUM_LENGTH_FRACTION = 0.78f            // length as fraction of canvas height
  val BOB_RADIUS_DP = 11f                         // ball size
  val STRING_WIDTH_DP = 1.5f                      // string thickness
  val STRING_COLOR = Color(0xFF555560)            // dim string
  val SUPPORT_BAR_COLOR = Color(0xFF888892)       // top support bar
  val COLOR_FIRST = Color(0xFFFFD740)             // leftmost pendulum color
  val COLOR_LAST = Color(0xFFE040FB)              // rightmost pendulum color
  val BG_COLOR = Color(0xFF101015)                // dark canvas background
  val TRAIL_LENGTH = 18                           // 0 (no trail) ↔ 60 (heavy trail)
  val TRAIL_ALPHA_HEAD = 0.55f                    // brightness of newest trail dot

  val density = LocalDensity.current
  val bobRadiusPx = with(density) { BOB_RADIUS_DP.dp.toPx() }
  val stringWidthPx = with(density) { STRING_WIDTH_DP.dp.toPx() }
  val supportBarWidthPx = with(density) { 2.5.dp.toPx() }
  val maxAngleRad = (MAX_ANGLE_DEG * PI.toFloat() / 180f)

  var time by remember { mutableStateOf(0f) }
  var tick by remember { mutableStateOf(0L) }

  val trails = remember(PENDULUM_COUNT) {
    Array(PENDULUM_COUNT) { ArrayDeque<Offset>() }
  }

  LaunchedEffect(Unit) {
    var lastFrame = 0L
    while (true) {
      androidx.compose.runtime.withFrameNanos { frameTime ->
        if (lastFrame == 0L) lastFrame = frameTime
        val deltaSec = (frameTime - lastFrame) / 1_000_000_000f
        lastFrame = frameTime
        time += deltaSec
        tick++
      }
    }
  }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(380.dp)) {
      val canvasWidthPx = with(density) { maxWidth.toPx() }
      val canvasHeightPx = with(density) { maxHeight.toPx() }

      Canvas(modifier = Modifier.fillMaxWidth().height(380.dp)) {
        val touch = tick

        drawRect(color = BG_COLOR, size = size)

        val pivotY = canvasHeightPx * PIVOT_TOP_FRACTION
        val length = canvasHeightPx * PENDULUM_LENGTH_FRACTION

        val horizontalPadding = canvasWidthPx * 0.06f
        val usableWidth = canvasWidthPx - horizontalPadding * 2f
        val spacing = if (PENDULUM_COUNT > 1) usableWidth / (PENDULUM_COUNT - 1) else 0f

        drawLine(
          color = SUPPORT_BAR_COLOR,
          start = Offset(horizontalPadding * 0.5f, pivotY),
          end = Offset(canvasWidthPx - horizontalPadding * 0.5f, pivotY),
          strokeWidth = supportBarWidthPx,
        )

        for (i in 0 until PENDULUM_COUNT) {
          val pivotX = horizontalPadding + i * spacing
          val oscillations = (BASE_OSCILLATIONS + i).toFloat()
          val omega = 2f * PI.toFloat() * oscillations / SYNC_PERIOD_SEC
          val theta = maxAngleRad * cos(omega * time)

          val bobX = pivotX + length * sin(theta)
          val bobY = pivotY + length * cos(theta)
          val bob = Offset(bobX, bobY)

          val gradientT = if (PENDULUM_COUNT > 1) i.toFloat() / (PENDULUM_COUNT - 1) else 0f
          val pendulumColor = lerp(COLOR_FIRST, COLOR_LAST, gradientT)

          val trail = trails[i]
          if (TRAIL_LENGTH > 0) {
            trail.addLast(bob)
            while (trail.size > TRAIL_LENGTH) {
              trail.removeFirst()
            }

            val trailSize = trail.size
            for (idx in 0 until trailSize) {
              val pos = trail[idx]
              val ageT = idx.toFloat() / trailSize.toFloat()
              val alpha = TRAIL_ALPHA_HEAD * ageT * ageT
              val trailRadius = bobRadiusPx * (0.35f + 0.55f * ageT)
              drawCircle(
                color = pendulumColor.copy(alpha = alpha),
                radius = trailRadius,
                center = pos,
              )
            }
          }

          drawLine(
            color = STRING_COLOR,
            start = Offset(pivotX, pivotY),
            end = bob,
            strokeWidth = stringWidthPx,
          )

          drawCircle(
            color = Color.Black.copy(alpha = 0.45f),
            radius = bobRadiusPx * 1.05f,
            center = Offset(bob.x + bobRadiusPx * 0.18f, bob.y + bobRadiusPx * 0.22f),
          )

          drawCircle(
            color = pendulumColor,
            radius = bobRadiusPx,
            center = bob,
          )

          drawCircle(
            color = pendulumColor.copy(alpha = 0.85f).let {
              lerp(it, Color.White, 0.55f)
            },
            radius = bobRadiusPx * 0.42f,
            center = Offset(bob.x - bobRadiusPx * 0.32f, bob.y - bobRadiusPx * 0.34f),
          )

          drawCircle(
            color = Color.White.copy(alpha = 0.85f),
            radius = bobRadiusPx * 0.18f,
            center = Offset(bob.x - bobRadiusPx * 0.38f, bob.y - bobRadiusPx * 0.40f),
          )

          drawCircle(
            color = pendulumColor,
            radius = bobRadiusPx * 0.18f,
            center = Offset(pivotX, pivotY),
          )
        }
      }
    }

    Text(
      "Pendulums sync, diverge, and resync every ${SYNC_PERIOD_SEC.toInt()}s. Try halving SYNC_PERIOD_SEC.",
      style = MaterialTheme.typography.labelSmall,
    )
  }
}
