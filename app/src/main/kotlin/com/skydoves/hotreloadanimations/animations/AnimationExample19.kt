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
import androidx.compose.foundation.layout.fillMaxSize
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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun AnimationExample19() {
  val ORB_COUNT = 16 // 2 (sparse) ↔ 12 (rich aurora)
  val ORB_GLOW_RADIUS_DP = 70f // 80 (small spots) ↔ 400 (dominating washes)
  val ORBIT_RADIUS_MIN_DP = 30f // 0 (orbs sit still) ↔ 200 (huge sweeps)
  val ORBIT_RADIUS_MAX_DP = 140f
  val ANGULAR_SPEED_MIN = 0.15f // rad/sec, small = slow drift
  val ANGULAR_SPEED_MAX = 0.55f // large = energetic swirl
  val VERTICAL_SQUASH = 0.6f // 1 (circles) ↔ 0.2 (very elliptical)
  val HUE_ROTATION_SPEED = 14f // deg/sec, 0 (static colors) ↔ 60 (rainbow churn)
  val PALETTE = listOf(
    Color(0xFFEBE361), // magenta
    Color(0xFF00E5FF), // cyan
    Color(0xFF9251D8), // purple
    Color(0xFFEC7DF0), // pink
    Color(0xFF18FFFF), // aqua
  )
  val BG_COLOR = Color(0xFF0B0E1A)
  val MID_STOP = 0.42f // controls glow falloff shape
  val MID_ALPHA = 0.55f

  val density = LocalDensity.current
  val glowRadiusPx = with(density) { ORB_GLOW_RADIUS_DP.dp.toPx() }
  val orbitMinPx = with(density) { ORBIT_RADIUS_MIN_DP.dp.toPx() }
  val orbitMaxPx = with(density) { ORBIT_RADIUS_MAX_DP.dp.toPx() }

  Column(
    Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxWidth()
        .height(380.dp),
    ) {
      val widthPx = with(density) { maxWidth.toPx() }
      val heightPx = with(density) { maxHeight.toPx() }

      val orbs = remember(widthPx, heightPx, ORB_COUNT) {
        val rng = Random(0xA0BEEFL)
        List(ORB_COUNT) { i ->
          OrbSpec(
            centerX = rng.nextFloat() * widthPx,
            centerY = rng.nextFloat() * heightPx,
            orbitT = rng.nextFloat(),
            angularT = rng.nextFloat(),
            phase = rng.nextFloat() * (2f * Math.PI.toFloat()),
            colorIndex = i,
            hueSeed = rng.nextFloat() * 360f,
          )
        }
      }

      var time by remember { mutableStateOf(0f) }
      var tick by remember { mutableStateOf(0L) }

      LaunchedEffect(Unit) {
        var lastNanos = 0L
        while (true) {
          withFrameNanos { now ->
            if (lastNanos == 0L) lastNanos = now
            val dt = (now - lastNanos) / 1_000_000_000f
            lastNanos = now
            time += dt
            tick++
          }
        }
      }

      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen),
      ) {
        @Suppress("UNUSED_EXPRESSION")
        tick

        drawRect(color = BG_COLOR, size = size)

        orbs.forEach { orb ->
          val orbitR = orbitMinPx + orb.orbitT * (orbitMaxPx - orbitMinPx).coerceAtLeast(0f)
          val angularSpeed = ANGULAR_SPEED_MIN +
            orb.angularT * (ANGULAR_SPEED_MAX - ANGULAR_SPEED_MIN).coerceAtLeast(0f)
          val baseColor = PALETTE[orb.colorIndex % PALETTE.size]
          val angle = orb.phase + angularSpeed * time
          val px = orb.centerX + cos(angle) * orbitR
          val py = orb.centerY + sin(angle) * orbitR * VERTICAL_SQUASH
          val shifted = shiftHue(baseColor, orb.hueSeed + time * HUE_ROTATION_SPEED)

          val brush = Brush.radialGradient(
            colorStops = arrayOf(
              0f to shifted,
              MID_STOP to shifted.copy(alpha = MID_ALPHA),
              1f to Color.Transparent,
            ),
            center = Offset(px, py),
            radius = glowRadiusPx,
          )

          drawCircle(
            brush = brush,
            radius = glowRadiusPx,
            center = Offset(px, py),
            blendMode = BlendMode.Plus,
          )
        }
      }
    }

    Text(
      "Color orbs orbit and blend additively. Change PALETTE to relight the room.",
      style = MaterialTheme.typography.labelSmall,
    )
  }
}

private data class OrbSpec(
  val centerX: Float,
  val centerY: Float,
  val orbitT: Float,
  val angularT: Float,
  val phase: Float,
  val colorIndex: Int,
  val hueSeed: Float,
)

private fun shiftHue(c: Color, deg: Float): Color {
  val hsv = FloatArray(3)
  android.graphics.Color.RGBToHSV(
    (c.red * 255).toInt(),
    (c.green * 255).toInt(),
    (c.blue * 255).toInt(),
    hsv,
  )
  hsv[0] = ((hsv[0] + deg) % 360f + 360f) % 360f
  val rgb = android.graphics.Color.HSVToColor(hsv)
  return Color(rgb).copy(alpha = c.alpha)
}
