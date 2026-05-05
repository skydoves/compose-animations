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

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimationExample7() {
  val PULSE_DURATION_MS = 600 // fast (200) ↔ slow (3000)
  val SCALE_MIN = 0.85f
  val SCALE_MAX = 1.15f
  val PULSE_EASING = FastOutSlowInEasing // try LinearEasing or EaseInOutCubic
  val ALPHA_MIN = 0.6f
  val ALPHA_MAX = 1.0f
  val HEART_FONT_SIZE_SP = 120
  // InfiniteTransition.animateFloat ignores animationSpec changes after first composition
  // (Compose only re-checks initialValue/targetValue). Wrap in key() so spec edits trigger
  // a fresh TransitionAnimationState with the new tween.
  val transition = rememberInfiniteTransition(label = "pulse")

  val scale by key(PULSE_DURATION_MS, PULSE_EASING) {
    transition.animateFloat(
      initialValue = SCALE_MIN,
      targetValue = SCALE_MAX,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = PULSE_DURATION_MS, easing = PULSE_EASING),
        repeatMode = RepeatMode.Reverse,
      ),
      label = "scale",
    )
  }

  val alpha by key(PULSE_DURATION_MS, PULSE_EASING) {
    transition.animateFloat(
      initialValue = ALPHA_MIN,
      targetValue = ALPHA_MAX,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = PULSE_DURATION_MS, easing = PULSE_EASING),
        repeatMode = RepeatMode.Reverse,
      ),
      label = "alpha",
    )
  }

  Column(
    modifier = Modifier.fillMaxWidth().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      text = "Pulsing Heart (rememberInfiniteTransition)",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.SemiBold,
    )
    Box(
      modifier = Modifier.fillMaxWidth().heightIn(min = 280.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = "♥",
        fontSize = HEART_FONT_SIZE_SP.sp,
        modifier = Modifier
          .scale(scale)
          .alpha(alpha),
      )
    }
  }
}
