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
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun AnimationExample17() {
  val LAYER_COUNT = 4                       // 1 (single line) ↔ 14 (deep ocean)
  val BASE_FREQUENCY = 0.010f               // 0.003 (slow rolling) ↔ 0.05 (choppy)
  val BASE_AMPLITUDE_DP = 18f               // 8 (calm) ↔ 80 (storm)
  val BASE_PHASE_SPEED = 2.2f               // 0.2 (slow drift) ↔ 4.0 (rapid)
  val FREQ_RAMP = 0.18f                     // each layer's freq multiplier increment
  val AMPL_FALLOFF = 0.08f                  // each layer's amplitude reduction
  val PHASE_RAMP = 0.35f                    // phase speed ramp per layer (parallax)
  val SECONDARY_RATIO = 2.7f                // second harmonic frequency multiplier
  val SECONDARY_AMP = 0.35f                 // 0 (clean sines) ↔ 1.5 (chaotic)
  val LAYER_COLOR_START = Color(0xFF7EC9EB) // front wave
  val LAYER_COLOR_END = Color(0xFF1A237E)   // back wave
  val LAYER_ALPHA_FRONT = 0.85f
  val LAYER_ALPHA_BACK = 0.25f
  val BG_TOP = Color(0xFF0A0E27)
  val BG_BOTTOM = Color(0xFFBFC2ED)
  val BASELINE_FRACTION = 0.55f             // 0.3 (waves on top) ↔ 0.85 (almost full)
  val SAMPLE_STEP_PX = 5f                   // lower = smoother but slower

  var time by remember { mutableStateOf(0f) }
  LaunchedEffect(Unit) {
    var lastNanos = 0L
    while (true) {
      withFrameNanos { nowNanos ->
        if (lastNanos != 0L) {
          val dtSec = (nowNanos - lastNanos) / 1_000_000_000f
          time += dtSec
        }
        lastNanos = nowNanos
      }
    }
  }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxWidth()
        .height(380.dp)
        .padding(horizontal = 16.dp),
    ) {
      val density = LocalDensity.current
      val widthPx = with(density) { maxWidth.toPx() }
      val heightPx = with(density) { maxHeight.toPx() }
      val baseAmplitudePx = with(density) { BASE_AMPLITUDE_DP.dp.toPx() }
      val baseline = heightPx * BASELINE_FRACTION
      val twoPi = (PI * 2.0).toFloat()
      val safeStep = if (SAMPLE_STEP_PX < 1f) 1f else SAMPLE_STEP_PX
      val sampleCount = ((widthPx / safeStep).toInt()).coerceAtLeast(2) + 1

      Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
          brush = Brush.verticalGradient(
            colors = listOf(BG_TOP, BG_BOTTOM),
            startY = 0f,
            endY = heightPx,
          ),
          topLeft = Offset(0f, 0f),
          size = size,
        )

        for (layerIndex in (LAYER_COUNT - 1) downTo 0) {
          val denom = (LAYER_COUNT - 1).coerceAtLeast(1).toFloat()
          val layerT = layerIndex.toFloat() / denom
          val frequency = BASE_FREQUENCY * (1f + layerIndex * FREQ_RAMP)
          val amplitude = baseAmplitudePx * (1f - layerIndex * AMPL_FALLOFF).coerceAtLeast(0.05f)
          val phaseSpeed = BASE_PHASE_SPEED * (1f + layerIndex * PHASE_RAMP)
          val phaseOffset = layerIndex * (twoPi / LAYER_COUNT.coerceAtLeast(1)) * 0.5f
          val phase = time * phaseSpeed + phaseOffset

          val baseColor = lerp(LAYER_COLOR_START, LAYER_COLOR_END, layerT)
          val alpha = LAYER_ALPHA_FRONT + (LAYER_ALPHA_BACK - LAYER_ALPHA_FRONT) * layerT
          val layerColor = baseColor.copy(alpha = alpha.coerceIn(0f, 1f))

          val path = Path()
          var x = 0f
          val firstY = waveY(
            x = x,
            baseline = baseline,
            frequency = frequency,
            amplitude = amplitude,
            phase = phase,
            secondaryRatio = SECONDARY_RATIO,
            secondaryAmp = SECONDARY_AMP,
          )
          path.moveTo(0f, firstY)

          var i = 1
          while (i < sampleCount) {
            x = (i * safeStep).coerceAtMost(widthPx)
            val y = waveY(
              x = x,
              baseline = baseline,
              frequency = frequency,
              amplitude = amplitude,
              phase = phase,
              secondaryRatio = SECONDARY_RATIO,
              secondaryAmp = SECONDARY_AMP,
            )
            path.lineTo(x, y)
            if (x >= widthPx) break
            i++
          }

          path.lineTo(widthPx, heightPx)
          path.lineTo(0f, heightPx)
          path.close()

          drawPath(path = path, color = layerColor)
        }
      }
    }

    Text(
      "Waves animate continuously. Tweak literals at top of file.",
      style = MaterialTheme.typography.labelSmall,
      modifier = Modifier.padding(horizontal = 16.dp),
    )
  }
}

private fun waveY(
  x: Float,
  baseline: Float,
  frequency: Float,
  amplitude: Float,
  phase: Float,
  secondaryRatio: Float,
  secondaryAmp: Float,
): Float {
  val primary = sin(x * frequency + phase) * amplitude
  val secondary = sin(x * frequency * secondaryRatio + phase * 1.7f) * amplitude * secondaryAmp
  return baseline + primary + secondary
}
