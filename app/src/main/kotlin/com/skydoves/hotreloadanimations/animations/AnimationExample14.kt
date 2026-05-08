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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimationExample14() {
  val ITEM_COUNT = 4 // try 3 (sparse) ↔ 8 (crowded)
  val RADIUS_DP = 110f // distance from center
  val STAGGER_MS = 50L // delay between satellites (0 = simultaneous)
  val SPRING_STIFFNESS = 500f // 100 (lazy) ↔ 1500 (snappy)
  val SPRING_DAMPING = 0.55f // 0.3 (very bouncy) ↔ 1.0 (no overshoot)
  val ARC_DEG = 180f // 180 = half circle, 360 = full circle

  val palette = listOf(
    Color(0xFFEF5350),
    Color(0xFF42A5F5),
    Color(0xFF66BB6A),
    Color(0xFFFFCA28),
    Color(0xFFAB47BC),
    Color(0xFF26A69A),
    Color(0xFFFF7043),
    Color(0xFF7E57C2),
  )
  val labels = listOf("★", "♥", "✦", "✿", "✱", "✪", "❖", "☎")

  var isOpen by remember { mutableStateOf(false) }
  val progresses = remember(ITEM_COUNT) { List(ITEM_COUNT) { Animatable(0f) } }
  val fabRotation by animateFloatAsState(
    targetValue = if (isOpen) 45f else 0f,
    animationSpec = spring(stiffness = SPRING_STIFFNESS, dampingRatio = SPRING_DAMPING),
    label = "fabRotation",
  )

  LaunchedEffect(isOpen) {
    progresses.forEachIndexed { i, p ->
      launch {
        val staggerIdx = if (isOpen) i else ITEM_COUNT - 1 - i
        delay(staggerIdx * STAGGER_MS)
        p.animateTo(
          targetValue = if (isOpen) 1f else 0f,
          animationSpec = spring(
            stiffness = SPRING_STIFFNESS,
            dampingRatio = SPRING_DAMPING,
          ),
        )
      }
    }
  }

  val density = LocalDensity.current
  val radiusPx = with(density) { RADIUS_DP.dp.toPx() }

  Column(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "Radial FAB Menu",
      style = MaterialTheme.typography.titleMedium,
    )
    Text(
      text = "Tap the center. Satellites scatter with staggered spring physics.",
      style = MaterialTheme.typography.bodySmall,
    )
    Box(
      modifier = Modifier.fillMaxWidth().height(320.dp),
      contentAlignment = Alignment.Center,
    ) {
      progresses.forEachIndexed { i, p ->
        val angleDeg = if (ITEM_COUNT == 1) {
          90f
        } else {
          val sweepStart = 180f + (180f - ARC_DEG) / 2f
          sweepStart + (i / (ITEM_COUNT - 1f)) * ARC_DEG
        }
        val rad = Math.toRadians(angleDeg.toDouble())
        val targetX = (radiusPx * cos(rad)).toFloat()
        val targetY = (radiusPx * sin(rad)).toFloat()
        Box(
          modifier = Modifier
            .size(56.dp)
            .graphicsLayer {
              val v = p.value
              translationX = targetX * v
              translationY = targetY * v
              val s = 0.4f + 0.6f * v
              scaleX = s
              scaleY = s
              alpha = v
            }
            .clip(CircleShape)
            .background(palette[i % palette.size])
            .clickable(enabled = isOpen) { isOpen = false },
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = labels[i % labels.size],
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
          )
        }
      }
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primary)
          .clickable { isOpen = !isOpen },
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = "+",
          color = MaterialTheme.colorScheme.onPrimary,
          fontSize = 36.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.graphicsLayer { rotationZ = fabRotation },
        )
      }
    }
  }
}
