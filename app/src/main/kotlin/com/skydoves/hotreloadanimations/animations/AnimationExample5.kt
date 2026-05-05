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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimationExample5() {
  val slideDurationMs = 650 // try 120 (snappy) / 800 (luxurious)
  val fadeDurationMs = 450 // try 0 (no fade) / 600 (long crossfade)
  val slideOffsetDivisor = 1 // 1 = full height slide, 2 = half, 4 = subtle

  // Add SizeTransform to AnimatedContent for animated width changes
  // e.g. AnimatedContent(... , transitionSpec = { ... using SizeTransform(...) })

  var count by remember { mutableIntStateOf(0) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp),
      contentAlignment = Alignment.Center,
    ) {
      AnimatedContent(
        targetState = count,
        transitionSpec = {
          val goingUp = targetState > initialState
          if (goingUp) {
            slideInVertically(
              animationSpec = tween(slideDurationMs),
              initialOffsetY = { it / slideOffsetDivisor },
            ) + fadeIn(animationSpec = tween(fadeDurationMs)) togetherWith
              slideOutVertically(
                animationSpec = tween(slideDurationMs),
                targetOffsetY = { -it / slideOffsetDivisor },
              ) + fadeOut(animationSpec = tween(fadeDurationMs))
          } else {
            slideInVertically(
              animationSpec = tween(slideDurationMs),
              initialOffsetY = { -it / slideOffsetDivisor },
            ) + fadeIn(animationSpec = tween(fadeDurationMs)) togetherWith
              slideOutVertically(
                animationSpec = tween(slideDurationMs),
                targetOffsetY = { it / slideOffsetDivisor },
              ) + fadeOut(animationSpec = tween(fadeDurationMs))
          }
        },
        label = "counter",
      ) { value ->
        Text(
          text = "$value",
          fontSize = 96.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    }
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
    ) {
      Button(onClick = { count -= 1 }) {
        Text(text = "−", fontSize = 24.sp)
      }
      Button(onClick = { count += 1 }) {
        Text(text = "+", fontSize = 24.sp)
      }
    }
  }
}
