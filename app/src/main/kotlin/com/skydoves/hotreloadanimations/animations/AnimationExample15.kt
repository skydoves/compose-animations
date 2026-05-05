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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimationExample15() {
  val FLIP_DURATION_MS = 700
  val FLIP_EASING = FastOutSlowInEasing  // try EaseInOutCubic
  val CAMERA_DISTANCE_FACTOR = 12f  // lower = more dramatic perspective

  val FRONT_COLOR = Color(0xFFE91E63)
  val BACK_COLOR = Color(0xFFFF7043)

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      "Tap the card to flip",
      modifier = Modifier.padding(horizontal = 8.dp),
    )

    var flipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
      targetValue = if (flipped) 180f else 0f,
      animationSpec = tween(durationMillis = FLIP_DURATION_MS, easing = FLIP_EASING),
      label = "flip",
    )
    val density = LocalDensity.current.density

    Box(
      modifier = Modifier.fillMaxWidth().height(320.dp),
      contentAlignment = Alignment.Center,
    ) {
      Card(
        modifier = Modifier
          .size(240.dp)
          .graphicsLayer {
            rotationY = rotation
            cameraDistance = CAMERA_DISTANCE_FACTOR * density
          }
          .clickable { flipped = !flipped },
        colors = CardDefaults.cardColors(
          containerColor = if (rotation <= 90f) FRONT_COLOR else BACK_COLOR,
        ),
      ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          if (rotation <= 90f) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Text("♥", fontSize = 96.sp, color = Color.White)
              Text("Compose", fontSize = 22.sp, color = Color.White)
            }
          } else {
            Column(
              modifier = Modifier.graphicsLayer { rotationY = 180f },
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              Text("✦", fontSize = 96.sp)
              Text("Saved!", fontSize = 22.sp, color = Color.White)
            }
          }
        }
      }
    }
  }
}
