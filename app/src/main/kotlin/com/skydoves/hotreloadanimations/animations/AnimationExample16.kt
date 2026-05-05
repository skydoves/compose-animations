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
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

private class Confetti(
  var x: Float,
  var y: Float,
  var vx: Float,
  var vy: Float,
  var rotation: Float,
  val rotSpeed: Float,
  var ageMs: Float,
  val lifetimeMs: Float,
  val color: Color,
  val w: Float,
  val h: Float,
  val wobblePhase: Float,
)

@Composable
fun AnimationExample16() {
  val BURST_COUNT = 70             // particles per tap
  val GRAVITY = 1100f              // px/sec², 200 (moon) ↔ 3000 (snappy)
  val SPEED_MIN = 700f
  val SPEED_MAX = 1500f
  val LAUNCH_ANGLE_DEG = -90f      // -90 = straight up, 0 = right
  val SPREAD_DEG = 110f             // narrower = jet, wider = burst
  val LIFETIME_MS_MIN = 1400f
  val LIFETIME_MS_MAX = 2600f
  val AIR_DRAG = 1.4f              // 0 (no drag) ↔ 4 (heavy drift down)
  val WOBBLE_AMP = 90f              // lateral flutter strength (px/sec)
  val WOBBLE_FREQ = 0.006f         // flutter rhythm
  val ROT_SPEED_MAX = 720f         // deg/sec
  val PARTICLE_W_DP = 7f
  val PARTICLE_H_DP = 14f
  val FADE_OUT_FRACTION = 0.3f     // last 30% of life fades to alpha 0
  val PALETTE = listOf(
    Color(0xFFFF5252),
    Color(0xFFFFEB3B),
    Color(0xFF40C4FF),
    Color(0xFF69F0AE),
    Color(0xFFE040FB),
    Color(0xFFFF6E40),
    Color(0xFFFFFFFF),
  )

  val particles = remember { mutableStateListOf<Confetti>() }
  var frameNanos by remember { mutableLongStateOf(0L) }

  Column(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Text(
      text = "Confetti Burst",
      style = MaterialTheme.typography.titleMedium,
    )
    Text(
      text = "Tap anywhere in the area to launch a burst.",
      style = MaterialTheme.typography.bodySmall,
    )
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxWidth()
        .height(380.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Color(0xFF101015)),
    ) {
      val density = LocalDensity.current
      val heightPx = with(density) { maxHeight.toPx() }
      val particleW = with(density) { PARTICLE_W_DP.dp.toPx() }
      val particleH = with(density) { PARTICLE_H_DP.dp.toPx() }

      LaunchedEffect(GRAVITY, AIR_DRAG, WOBBLE_AMP, WOBBLE_FREQ, heightPx) {
        var lastFrame = withFrameNanos { it }
        while (true) {
          val now = withFrameNanos { it }
          val dtMs = min((now - lastFrame) / 1_000_000f, 50f)
          val dtSec = dtMs / 1000f
          lastFrame = now

          val damping = exp(-AIR_DRAG * dtSec)
          var i = 0
          while (i < particles.size) {
            val p = particles[i]
            p.ageMs += dtMs
            if (p.ageMs >= p.lifetimeMs || p.y > heightPx + 120f) {
              particles.removeAt(i)
              continue
            }
            p.vy += GRAVITY * dtSec
            p.vx *= damping
            p.vy *= damping
            val wobble = sin(p.ageMs * WOBBLE_FREQ + p.wobblePhase) * WOBBLE_AMP
            p.x += (p.vx + wobble) * dtSec
            p.y += p.vy * dtSec
            p.rotation += p.rotSpeed * dtSec
            i++
          }
          frameNanos = now
        }
      }

      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .pointerInput(
            BURST_COUNT, SPEED_MIN, SPEED_MAX, SPREAD_DEG, LAUNCH_ANGLE_DEG,
            LIFETIME_MS_MIN, LIFETIME_MS_MAX, ROT_SPEED_MAX,
            particleW, particleH,
          ) {
            detectTapGestures { offset ->
              val baseRad = Math.toRadians(LAUNCH_ANGLE_DEG.toDouble()).toFloat()
              val spreadRad = Math.toRadians(SPREAD_DEG.toDouble() / 2.0).toFloat()
              repeat(BURST_COUNT) {
                val angle = baseRad + (Random.nextFloat() * 2f - 1f) * spreadRad
                val speed = SPEED_MIN + Random.nextFloat() * (SPEED_MAX - SPEED_MIN)
                val sizeJitter = 0.7f + Random.nextFloat() * 0.6f
                particles.add(
                  Confetti(
                    x = offset.x,
                    y = offset.y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    rotation = Random.nextFloat() * 360f,
                    rotSpeed = (Random.nextFloat() * 2f - 1f) * ROT_SPEED_MAX,
                    ageMs = 0f,
                    lifetimeMs = LIFETIME_MS_MIN +
                      Random.nextFloat() * (LIFETIME_MS_MAX - LIFETIME_MS_MIN),
                    color = PALETTE[Random.nextInt(PALETTE.size)],
                    w = particleW * sizeJitter,
                    h = particleH * sizeJitter,
                    wobblePhase = Random.nextFloat() * (2f * Math.PI.toFloat()),
                  ),
                )
              }
            }
          },
      ) {
        // Subscribe to per-frame redraws.
        @Suppress("UNUSED_EXPRESSION") frameNanos
        for (p in particles) {
          val lifeFraction = (1f - p.ageMs / p.lifetimeMs).coerceIn(0f, 1f)
          val alpha = if (lifeFraction < FADE_OUT_FRACTION) {
            lifeFraction / FADE_OUT_FRACTION
          } else {
            1f
          }
          rotate(degrees = p.rotation, pivot = Offset(p.x, p.y)) {
            drawRect(
              color = p.color.copy(alpha = alpha),
              topLeft = Offset(p.x - p.w / 2f, p.y - p.h / 2f),
              size = Size(p.w, p.h),
            )
          }
        }
      }
    }

    Text(
      text = "particles: ${particles.size}",
      style = MaterialTheme.typography.labelSmall,
    )
  }
}
