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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.hypot
import kotlin.math.max
import kotlin.random.Random

@Composable
fun AnimationExample18() {
  val ballCount = 7 // 3 (sparse) ↔ 14 (chaos)
  val ballRadiusMinDp = 28f // small balls
  val ballRadiusMaxDp = 52f // big balls, variation matters
  val glowRadiusMult = 2.2f // 1.2 (crisp) ↔ 4.0 (heavy fusion)
  val maxSpeedDpPerSec = 90f // slow lava lamp ↔ frantic
  val driftNoise = 50f // 0 (linear) ↔ 200 (jittery)
  val palette = listOf(
    Color(0xFF69F0AE),
    Color(0xFF00BCD4),
    Color(0xFF7C4DFF),
    Color(0xFFFF4081),
  )
  val bgColor = Color(0xFF080812) // dark shows glow best
  val showInnerCore = true // false = pure soft blobs
  val innerCoreFraction = 0.35f // size of the crisp inner core
  val gradientMidStop = 0.45f // 0.2 (sharp) ↔ 0.8 (very soft halo)
  val gradientMidAlpha = 0.70f // alpha at the mid stop

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
      val rMinPx = with(density) { ballRadiusMinDp.dp.toPx() }
      val rMaxPx = with(density) { ballRadiusMaxDp.dp.toPx() }
      val maxSpeedPx = with(density) { maxSpeedDpPerSec.dp.toPx() }
      val driftNoisePx = with(density) { driftNoise.dp.toPx() }

      val balls = remember(widthPx, heightPx, ballCount, rMinPx, rMaxPx) {
        val rng = Random(0xB10B5L)
        List(ballCount) { idx ->
          val r = rMinPx + rng.nextFloat() * (rMaxPx - rMinPx)
          Ball18(
            x = rng.nextFloat() * max(1f, widthPx - 2f * r) + r,
            y = rng.nextFloat() * max(1f, heightPx - 2f * r) + r,
            vx = (rng.nextFloat() * 2f - 1f) * maxSpeedPx * 0.6f,
            vy = (rng.nextFloat() * 2f - 1f) * maxSpeedPx * 0.6f,
            r = r,
            colorIndex = idx,
          )
        }
      }

      var tick by remember { mutableLongStateOf(0L) }

      LaunchedEffect(ballCount, ballRadiusMinDp, ballRadiusMaxDp, maxSpeedPx, driftNoisePx) {
        var lastNanos = 0L
        val rng = Random(0xF1A5C0L)
        while (true) {
          withFrameNanos { now ->
            val dt = if (lastNanos ==
              0L
            ) {
              0f
            } else {
              ((now - lastNanos) / 1_000_000_000f).coerceAtMost(0.05f)
            }
            lastNanos = now
            if (dt > 0f) {
              for (b in balls) {
                b.vx += (rng.nextFloat() * 2f - 1f) * driftNoisePx * dt
                b.vy += (rng.nextFloat() * 2f - 1f) * driftNoisePx * dt
                val speed = hypot(b.vx, b.vy)
                if (speed > maxSpeedPx) {
                  val s = maxSpeedPx / speed
                  b.vx *= s
                  b.vy *= s
                }
                b.x += b.vx * dt
                b.y += b.vy * dt
                if (b.x - b.r < 0f) {
                  b.x = b.r
                  b.vx = -b.vx
                }
                if (b.x + b.r > widthPx) {
                  b.x = widthPx - b.r
                  b.vx = -b.vx
                }
                if (b.y - b.r < 0f) {
                  b.y = b.r
                  b.vy = -b.vy
                }
                if (b.y + b.r > heightPx) {
                  b.y = heightPx - b.r
                  b.vy = -b.vy
                }
              }
              tick++
            }
          }
        }
      }

      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(380.dp)
          .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen),
      ) {
        val touch = tick
        drawRect(color = bgColor, size = size)
        for (b in balls) {
          val glowRadius = b.r * glowRadiusMult
          val center = Offset(b.x, b.y)
          val core = palette[b.colorIndex % palette.size]
          val brush = Brush.radialGradient(
            colorStops = arrayOf(
              0f to core,
              gradientMidStop to core.copy(alpha = gradientMidAlpha),
              1f to Color.Transparent,
            ),
            center = center,
            radius = glowRadius,
          )
          drawCircle(
            brush = brush,
            radius = glowRadius,
            center = center,
            blendMode = BlendMode.Plus,
          )
        }
        if (showInnerCore) {
          for (b in balls) {
            val coreRadius = b.r * innerCoreFraction
            val ballColor = palette[b.colorIndex % palette.size]
            drawCircle(
              color = ballColor.copy(alpha = 0.95f),
              radius = max(1f, coreRadius),
              center = Offset(b.x, b.y),
              blendMode = BlendMode.Plus,
            )
            drawCircle(
              color = Color.White.copy(alpha = 0.55f),
              radius = max(1f, coreRadius * 0.45f),
              center = Offset(
                b.x - coreRadius * 0.25f,
                b.y - coreRadius * 0.25f,
              ),
              blendMode = BlendMode.Plus,
            )
          }
        }
      }
    }

    Text(
      "Drifting blobs fuse on overlap with additive blend.",
      style = MaterialTheme.typography.labelSmall,
      modifier = Modifier.padding(horizontal = 16.dp),
    )
  }
}

private data class Ball18(
  var x: Float,
  var y: Float,
  var vx: Float,
  var vy: Float,
  val r: Float,
  val colorIndex: Int,
)
