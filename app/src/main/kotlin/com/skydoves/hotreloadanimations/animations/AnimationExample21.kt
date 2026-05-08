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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun AnimationExample21() {
  val DROP_COUNT = 100 // 40 (drizzle) ↔ 500 (downpour)
  val ANGLE_DEG = 100f // 90 = straight down, 135 = strong left slant; 95 to 120 looks like wind blown rain
  val SPEED_MIN_DP_PER_SEC = 180f // slowest drop (parallax depth)
  val SPEED_MAX_DP_PER_SEC = 360f // fastest drop (foreground)
  val STREAK_LENGTH_MIN_DP = 14f
  val STREAK_LENGTH_MAX_DP = 28f // longer = more motion blur
  val STROKE_WIDTH_DP = 1.1f // thin (0.6) ↔ thick (3.0)
  val RAIN_HEAD_COLOR = Color(0xFFB3E5FC) // bright streak head
  val RAIN_TAIL_ALPHA = 0.0f // 0 = clean fade to transparent tail
  val SPLASH_ENABLED = true // tiny splash mark on the bottom edge
  val SPLASH_RADIUS_DP = 2.5f
  val BG_TOP = Color(0xFF0B1420)
  val BG_BOTTOM = Color(0xFF152033)

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxWidth()
        .height(380.dp)
        .padding(horizontal = 12.dp),
    ) {
      val density = LocalDensity.current
      val widthPx = with(density) { maxWidth.toPx() }
      val heightPx = with(density) { maxHeight.toPx() }
      val streakMinPx = with(density) { STREAK_LENGTH_MIN_DP.dp.toPx() }
      val streakMaxPx = with(density) { STREAK_LENGTH_MAX_DP.dp.toPx() }
      val strokeWidthPx = with(density) { STROKE_WIDTH_DP.dp.toPx() }
      val splashRadiusPx = with(density) { SPLASH_RADIUS_DP.dp.toPx() }
      val speedMinPx = with(density) { SPEED_MIN_DP_PER_SEC.dp.toPx() }
      val speedMaxPx = with(density) { SPEED_MAX_DP_PER_SEC.dp.toPx() }

      val angleRad = (ANGLE_DEG * PI / 180.0).toFloat()
      val dirX = cos(angleRad)
      val dirY = sin(angleRad)

      val safeDirY = if (dirY > 0.05f) dirY else 0.05f
      val horizontalDrift = kotlin.math.abs(dirX) * heightPx / safeDirY
      val spawnXMin = -horizontalDrift
      val spawnXMax = widthPx + horizontalDrift
      val spawnXSpan = spawnXMax - spawnXMin

      val drops = remember(widthPx, heightPx, DROP_COUNT, streakMinPx, streakMaxPx, speedMinPx, speedMaxPx, dirX, dirY) {
        val rng = Random(0xCAFEBABEL)
        List(DROP_COUNT) {
          Drop21(
            x = spawnXMin + rng.nextFloat() * spawnXSpan,
            y = rng.nextFloat() * heightPx,
            speed = speedMinPx + rng.nextFloat() * (speedMaxPx - speedMinPx),
            length = streakMinPx + rng.nextFloat() * (streakMaxPx - streakMinPx),
          )
        }
      }

      var tick by remember { mutableLongStateOf(0L) }

      LaunchedEffect(DROP_COUNT, widthPx, heightPx, speedMinPx, speedMaxPx, dirX, dirY) {
        var lastNanos = 0L
        val rng = Random(0xBEEFCAFEL)
        while (true) {
          withFrameNanos { now ->
            val dt = if (lastNanos == 0L) 0f else ((now - lastNanos) / 1_000_000_000f).coerceAtMost(0.05f)
            lastNanos = now
            for (d in drops) {
              d.x += dirX * d.speed * dt
              d.y += dirY * d.speed * dt
              if (d.y - d.length > heightPx || d.x + d.length < spawnXMin || d.x - d.length > spawnXMax) {
                d.x = spawnXMin + rng.nextFloat() * spawnXSpan
                d.y = -d.length - rng.nextFloat() * heightPx * 0.3f
              }
            }
            tick++
          }
        }
      }

      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(380.dp),
      ) {
        val touch = tick
        drawRect(
          brush = Brush.verticalGradient(listOf(BG_TOP, BG_BOTTOM)),
          size = size,
        )

        for (d in drops) {
          val headX = d.x
          val headY = d.y
          val tailX = headX - dirX * d.length
          val tailY = headY - dirY * d.length

          drawLine(
            brush = Brush.linearGradient(
              colors = listOf(RAIN_HEAD_COLOR.copy(alpha = RAIN_TAIL_ALPHA), RAIN_HEAD_COLOR),
              start = Offset(tailX, tailY),
              end = Offset(headX, headY),
            ),
            start = Offset(tailX, tailY),
            end = Offset(headX, headY),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round,
          )

          if (SPLASH_ENABLED && headY in (size.height - 2f)..(size.height + 2f)) {
            drawCircle(
              color = RAIN_HEAD_COLOR.copy(alpha = 0.7f),
              radius = splashRadiusPx,
              center = Offset(headX, size.height - 1f),
            )
          }
        }
      }
    }

    Text(
      "Continuous diagonal rain. Tweak ANGLE_DEG (90 = straight down), DROP_COUNT, or speed range.",
      style = MaterialTheme.typography.labelSmall,
      modifier = Modifier.padding(horizontal = 16.dp),
    )
  }
}

private data class Drop21(
  var x: Float,
  var y: Float,
  val speed: Float,
  val length: Float,
)
